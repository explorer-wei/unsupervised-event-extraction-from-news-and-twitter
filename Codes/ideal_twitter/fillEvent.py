__author__ = 'ericshape'

import MySQLdb as mdb
import sys
import pprint

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
    cur.execute("select id, count(id) as c from z_53_ner group by id limit 1000")
    # cur.execute("select id, count(id) as c from news_paragraph_ner group by id order by c desc")
    eventNER_summary = cur.fetchall()

    with open(OutputFileName, 'wb') as outputFile:
        #Extract the top Twitter UIDs which have Name Entities
        for eventIDs in eventNER_summary:
            twitterID = eventIDs[0]

            # init var
            When = {}
            Who = {}
            Where = {}
            EventFormat = {}

            # get all the NER results of this instance
            cur.execute("select * from z_53_ner where id = %s", twitterID)
            NER_query = cur.fetchall()
            for NER_instance in NER_query:
                if NER_instance[2] == 'LOCATION':
                    whereInfo = NER_instance[3]
                    Where[whereInfo] = NER_instance[0]

                if NER_instance[2] == 'PERSON' or NER_instance[2] == 'ORGANIZATION':
                    whoInfo = NER_instance[3]
                    Who[whoInfo] = NER_instance[0]

                if NER_instance[2] == 'DATE' or NER_instance[2] == 'TIME':
                    whenInfo = NER_instance[3]
                    When[whenInfo] = NER_instance[0]

            # get topic mapping from database query
            TopicResults = {}
            # get all the topic mapping of this instance
            cur.execute("select * from z_53_topics_mapping where id = %s", twitterID)
            topicMapping_query = cur.fetchall()
            for topicMapping_instance in topicMapping_query:
                freq = topicMapping_instance[2]
                if freq > 0.3:
                    TopicResults[topicMapping_instance[1]] = freq

            # Assign to event format
            EventFormat['WHERE'] = Where
            EventFormat['WHO'] = Who
            EventFormat['WHEN'] = When
            EventFormat['TOPIC'] = TopicResults

            print >> outputFile, EventFormat


except mdb.Error, e:
    print "Error %d: %s" % (e.args[0],e.args[1])
    sys.exit(1)

finally:
    if con:
        con.close()