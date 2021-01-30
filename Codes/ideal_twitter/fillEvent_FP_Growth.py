__author__ = 'ericshape'

import MySQLdb as mdb
import sys
import pprint
from fp_growth import find_frequent_itemsets
import csv
import datetime

# Connect with Remote
# init connector
con = None
OutputFileName = 'EventOutput.json'
try:
    # init connector
    con = mdb.connect(host="128.173.237.170", user="vts", passwd="vts_twitter", db="twapperkeeper_2")
    # Verify whether the database is connected, output current database version.
    cur = con.cursor()
    cur.execute("SELECT VERSION()")
    ver = cur.fetchone()
    print "Database version : %s " % ver

    #get all the Twitter UIDs which have Name Entities
    cur.execute("select id, count(id) as c from z_166_ner group by id limit 1000")
    # cur.execute("select id, count(id) as c from news_paragraph_ner group by id order by c desc")
    eventNER_summary = cur.fetchall()

    with open(OutputFileName, 'wb') as outputFile:
        #Extract the top Twitter UIDs which have Name Entities
        csvWriter = csv.writer(outputFile,delimiter=',', quotechar='"')

        # currently, we have 20 topics in our tweets training process.
        for topic_index in range(0,20):
            # get topic mapping from database query
            Twitter_id_set = set()
            TopicResults = {}
            # get all the topic mapping of this instance
            cur.execute("select id from z_166_topics_mapping where tid = %s and freq>0.5", topic_index)
            topicMapping_query = cur.fetchall()
            for topicMapping_instance in topicMapping_query:
                Twitter_id_set.add(topicMapping_instance[0])
            print Twitter_id_set

            for twitterID in Twitter_id_set:

                itemSet = set()
                # init var
                When = {}
                Who = {}
                Where = {}
                EventFormat = {}

                # get all the NER results of this instance
                cur.execute("select * from z_166_ner where id = %s", twitterID)
                NER_query = cur.fetchall()
                for NER_instance in NER_query:
                    if NER_instance[2] == 'LOCATION':
                        whereInfo = NER_instance[3]
                        Where[whereInfo] = NER_instance[0]
                        itemSet.add(NER_instance[3])

                    if NER_instance[2] == 'PERSON' or NER_instance[2] == 'ORGANIZATION':
                        whoInfo = NER_instance[3]
                        Who[whoInfo] = NER_instance[0]
                        itemSet.add(NER_instance[3])

                    # if NER_instance[2] == 'DATE' or NER_instance[2] == 'TIME':
                    #     whenInfo = NER_instance[3]
                    #     When[whenInfo] = NER_instance[0]

                    #query the send time of tweets in tweets table.
                    if len(itemSet) > 1:
                        cur.execute("select z_166.time from z_166 where id = %s", twitterID)
                        Tweets_query = cur.fetchall()
                        tweetsDate = datetime.datetime.fromtimestamp(int(Tweets_query[0][0])).strftime('%b %d, %Y')
                        itemSet.add(tweetsDate)

                # Assign to event format
                # EventFormat['WHERE'] = Where
                # EventFormat['WHO'] = Who
                # EventFormat['WHEN'] = When
                # EventFormat['TOPIC'] = TopicResults

                #print >> outputFile, EventFormat

                #convert set to a list
                outputStringList = list(itemSet)
                if len(outputStringList) >= 3 and len(Where)>0 and len(Who)>0:
                    outputString = ';'.join(outputStringList)
                    print outputString
                    csvWriter.writerow([outputString])


    f = open(OutputFileName)
    try:
        for itemset, support in find_frequent_itemsets(csv.reader(f), 2, True):
            print '{' + ', '.join(itemset) + '} ' + str(support)
    finally:
        f.close()


except mdb.Error, e:
    print "Error %d: %s" % (e.args[0],e.args[1])
    sys.exit(1)

finally:
    if con:
        con.close()