__author__ = 'ericshape'
import re

outputFileName = 'rssURL.out'
fOutputFile = open(outputFileName,'w')

subjectOutputFileName = 'subject.out'
sujectOutputFile = open(subjectOutputFileName,'w')

with open("mbox1.dat", "r") as f:
    rssLinkTag = "X-Mail-Rss-Article-Url: "
    subjectTag = "Subject: "
    for line in f:
        if subjectTag in line:
            print >> sujectOutputFile, line[len(subjectTag):len(line)-1]

        if (rssLinkTag in line) and re.search(r"(([\w]+:)?//)?(([\d\w]|%[a-fA-f\d]{2,2})+(:([\d\w]|%[a-fA-f\d]{2,2})+)?@)?([\d\w][-\d\w]{0,253}[\d\w]\.)+[\w]{2,4}(:[\d]+)?(/([-+_~.\d\w]|%[a-fA-f\d]{2,2})*)*(\?(&?([-+_~.\d\w]|%[a-fA-f\d]{2,2})=?)*)?(#([-+_~.\d\w]|%[a-fA-f\d]{2,2})*)?", line):
            print >> fOutputFile, line[len(rssLinkTag):len(line)-1]

