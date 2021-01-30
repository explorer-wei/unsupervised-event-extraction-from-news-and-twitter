__author__ = 'ericshape'

import json
from pprint import pprint

outputFileName = 'graphEdges.json'
fOutputFile = open(outputFileName,'w')

i = 0
with open("user_info_complete_merged-0412_10") as f:
    for line in f:
        requestData = json.loads(line)
        pprint(requestData, fOutputFile)

        # followerGroup = requestData['followers']
        # if followerGroup != [] and i < 100:
        #     i +=1
        #     friend = requestData['id']
        #     for follower in followerGroup:
        #         print >> fOutputFile, friend, ';' , follower
