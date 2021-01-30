__author__ = 'ericshape'
# -*- coding: utf-8 -*-
import re
import string
import csv
import ner
import sys

def extract_entity_names(t):
    entity_names = []

    if hasattr(t, 'node') and t.node:
        if t.node == 'NE':
            entity_names.append(' '.join([child[0] for child in t]))
        else:
            for child in t:
                entity_names.extend(extract_entity_names(child))

    return entity_names


def preprocess(inputFileNames, outputFileName):
    numOmitted=0
    with open(outputFileName,'w') as outputFile:
        csvWriter = csv.writer(outputFile,delimiter=',', quotechar='"')
        for inputFileName in inputFileNames:
            with open(inputFileName, 'rU') as inputFile:
                spreadsheet = csv.reader(inputFile,delimiter=',')
                for row in spreadsheet:
                    #if len(row)==2:
                        trimmed = row[2]
                        #trimmed=re.sub(r'\d+(?:[.,:/-]\d+)+','',trimmed)
                        #trimmed=re.sub(r'(?:[:;][ -=]?|=)[ Dp (|)][ D ()]*| <3+','',trimmed)
                        trimmed=re.sub(r'(?:https?\:\/\/|www\.)[a-zA-Z0-9/.?=&\-#]*[a-zA-Z0-9/]','',trimmed)
                        trimmed=re.sub(r'[#@]\w+','',trimmed)
                        trimmed=re.sub(r'[$£¤¥¢§@&%\+~]+','',trimmed)
                        #trimmed=re.sub(r'[,.=_!\-\\\?]',' ',trimmed)
                        trimmed=re.sub(r'\\n','',trimmed)
                        flag=True
                        for c in trimmed:
                            if c not in string.printable:
                                numOmitted+=1
                                flag=False
                        if flag:
                            csvWriter.writerow([row[0],trimmed])
    print "Omitted",numOmitted,"tweets that contains non-ASCII characters."



def genNER(OutputFileName, inputFileName):

    NER_uid = 0
    tagger = ner.SocketNER(host='localhost', port=8080)
# because we are doing NER, so we don't need to remove stopwords
#    stoplist = set('\",\',rt,\'tis,\'twas,able,about,across,after,ain\'t,all,almost,also,among,and,any,are,aren\'t,because,been,but,can,can\'t,cannot,could,could\'ve,couldn\'t,dear,did,didn\'t,does,doesn\'t,don\'t,either,else,ever,every,for,from,get,got,had,has,hasn\'t,have,he\'d,he\'ll,he\'s,her,hers,him,his,how,how\'d,how\'ll,how\'s,however,i\'d,i\'ll,i\'m,i\'ve,into,isn\'t,it\'s,its,just,least,let,like,likely,may,might,might\'ve,mightn\'t,most,must,must\'ve,mustn\'t,neither,nor,not,off,often,only,other,our,own,rather,said,say,says,shan\'t,she,she\'d,she\'ll,she\'s,should,should\'ve,shouldn\'t,since,some,than,that,that\'ll,that\'s,the,their,them,then,there,there\'s,these,they,they\'d,they\'ll,they\'re,they\'ve,this,tis,too,twas,wants,was,wasn\'t,we\'d,we\'ll,we\'re,were,weren\'t,what,what\'d,what\'s,when,when,when\'d,when\'ll,when\'s,where,where\'d,where\'ll,where\'s,which,while,who,who\'d,who\'ll,who\'s,whom,why,why\'d,why\'ll,why\'s,will,with,won\'t,would,would\'ve,wouldn\'t,yet,you,you\'d,you\'ll,you\'re,you\'ve,your,a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your'.split(","))

    with open(OutputFileName, 'wb') as outputFile:
        csvWriter = csv.writer(outputFile, delimiter=',',quotechar='"')
        with open (inputFileName,'rb') as inputFile:
            csvReader = csv.reader(inputFile,delimiter=',',quotechar='"')
            for row in csvReader:
                if len(row)>1:
                    NER_result_array = tagger.get_entities(row[1])
                    for NER_class in NER_result_array:
                        for NER_item in NER_result_array[NER_class]:
                            # print in CSV
                            csvWriter.writerow([NER_uid, row[0], NER_class, NER_item])
                            NER_uid += 1
                            if NER_uid%10 == 0:
                                print NER_uid, "have done."

preprocess(['news_paragraph.csv'], 'tweets_NER_trimmed')
genNER('tweetsNER', 'tweets_NER_trimmed')


