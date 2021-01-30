# -*- coding: utf-8 -*-
import sys
from gensim import corpora, models, similarities
import re
import string
from nltk.corpus import stopwords
import csv

numTopics = 20

def preprocess(inputFileNames, outputFileName):
    numOmitted=0
    with open(outputFileName,'w') as outputFile:
        csvWriter = csv.writer(outputFile,delimiter=',', quotechar='"')
        for inputFileName in inputFileNames:
            with open(inputFileName, 'rU') as inputFile:
                spreadsheet = csv.reader(inputFile,delimiter=',')
                for row in spreadsheet:
                    if len(row)==2:
                        trimmed = row[1]
                        trimmed=re.sub(r'\d+(?:[.,:/-]\d+)+','',trimmed)
                        trimmed=re.sub(r'(?:[:;][ -=]?|=)[ Dp (|)][ D ()]*| <3+','',trimmed)
                        trimmed=re.sub(r'(?:https?\:\/\/|www\.)[a-zA-Z0-9/.?=&\-#]*[a-zA-Z0-9/]','',trimmed)
                        trimmed=re.sub(r'[#@]\w+','',trimmed)
                        trimmed=re.sub(r'[$£¤¥¢§@&%\+~]+','',trimmed)
                        trimmed=re.sub(r'[,.=_!\-\\\?]',' ',trimmed)
                        trimmed=re.sub(r'\\n','',trimmed)
                        flag=True
                        for c in trimmed:
                            if c not in string.printable:
                                numOmitted+=1
                                flag=False
                        if flag:
                            csvWriter.writerow([row[0],trimmed])
    print "Omitted",numOmitted,"tweets that contains non-ASCII characters."


def genDict(inputFileName):
    stoplist = set('for a of the and to in'.split())

    stoplist = set('\",\',rt,\'tis,\'twas,able,about,across,after,ain\'t,all,almost,also,among,and,any,are,aren\'t,because,been,but,can,can\'t,cannot,could,could\'ve,couldn\'t,dear,did,didn\'t,does,doesn\'t,don\'t,either,else,ever,every,for,from,get,got,had,has,hasn\'t,have,he\'d,he\'ll,he\'s,her,hers,him,his,how,how\'d,how\'ll,how\'s,however,i\'d,i\'ll,i\'m,i\'ve,into,isn\'t,it\'s,its,just,least,let,like,likely,may,might,might\'ve,mightn\'t,most,must,must\'ve,mustn\'t,neither,nor,not,off,often,only,other,our,own,rather,said,say,says,shan\'t,she,she\'d,she\'ll,she\'s,should,should\'ve,shouldn\'t,since,some,than,that,that\'ll,that\'s,the,their,them,then,there,there\'s,these,they,they\'d,they\'ll,they\'re,they\'ve,this,tis,too,twas,wants,was,wasn\'t,we\'d,we\'ll,we\'re,were,weren\'t,what,what\'d,what\'s,when,when,when\'d,when\'ll,when\'s,where,where\'d,where\'ll,where\'s,which,while,who,who\'d,who\'ll,who\'s,whom,why,why\'d,why\'ll,why\'s,will,with,won\'t,would,would\'ve,wouldn\'t,yet,you,you\'d,you\'ll,you\'re,you\'ve,your,a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your'.split(","))

#     stoplist = stopwords.words('english')
    # collect statistics about all tokens
    
    dictionary = corpora.Dictionary()

    with open (inputFileName,'rb') as inputFile:
        csvReader = csv.reader(inputFile,delimiter=',',quotechar='"')
        for row in csvReader:
            if len(row)>1:
                dictionary.add_documents([row[1].split()])


    # remove stop words and words that appear only once
    stop_ids = [dictionary.token2id[stopword] for stopword in stoplist if stopword in dictionary.token2id]
    once_ids = [tokenid for tokenid, docfreq in dictionary.dfs.iteritems() if docfreq == 1]
    dictionary.filter_tokens(stop_ids + once_ids) # remove stop words and words that appear only once
    dictionary.compactify() # remove gaps in id sequence after words that were removed
    dictionary.save('ldaDict.dict')
    return dictionary


def performLDA(dic,inputFileName):
    mm=[]
    with open(inputFileName) as inputFile:
        csvReader = csv.reader(inputFile,delimiter=',',quotechar='"')
        for row in csvReader:
            mm.append(dic.doc2bow(row[1].lower().split()))
    lda = models.ldamodel.LdaModel(corpus=mm, id2word=dic, num_topics=numTopics, update_every=1, chunksize=10000, passes=1)
    return lda

preprocess(sys.argv[1:], 'input_trimmed')
dic= genDict('input_trimmed')
lda=performLDA(dic,'input_trimmed')
lda.save('lda-model.lda')
with open("topics.csv",'wb') as topicFile:
    csvWriter = csv.writer(topicFile,delimiter=',', quotechar='"')
    counter = 0
    for topic in lda.print_topics(numTopics):
        csvWriter.writerow([str(counter),str(topic)]);
        counter += 1



