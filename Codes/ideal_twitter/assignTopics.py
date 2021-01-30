from gensim import corpora, models, similarities
import csv

dic = corpora.dictionary.Dictionary.load('ldaDict.dict')
lda = models.LdaModel.load('lda-model.lda')
with open("tweetsBelongToTopic", 'wb') as outputFile:
    csvWriter = csv.writer(outputFile, delimiter=',',quotechar='"')
    with open("input_trimmed", 'rb') as inputFile:
        csvReader = csv.reader(inputFile,delimiter=',',quotechar='"')
        for row in csvReader:
            lis=lda[dic.doc2bow(row[1].lower().split())]
            for tid,freq in lis:
                csvWriter.writerow([row[0],tid, freq])


