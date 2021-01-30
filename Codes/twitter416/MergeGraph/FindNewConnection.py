__author__ = 'ericshape'
import json
from pprint import pprint

outputFileName = 'newConnection.csv'
fOutputFile = open(outputFileName,'w+')

followerDict = {}


previousIdSet = set()
with open("/Users/ericshape/Downloads/user_info_complete_merged-0407") as f:
    i = 0
    for line in f:
        requestData = json.loads(line)
        #pprint(requestData, fOutputFile)

        followerGroup = requestData['followers']
        if followerGroup != [] and i < 4000:
            i +=1
            friend = requestData['id']
            #for follower in followerGroup:
                #print >> fOutputFile, friend, ';' , follower
            followerDict[friend] = followerGroup
            print friend
            previousIdSet.add(friend)
print "size of ID Set of 0407 :", len(previousIdSet)

newIdSet = set()
with open("/Users/ericshape/Downloads/user_info_complete_merged-0408") as f:
    for line in f:
        requestData = json.loads(line)

        followerGroup = requestData['followers']
        if followerGroup != []:
            i +=1
            friend = requestData['id']
            newIdSet.add(friend)

            followerInfo = followerDict.get(friend)

            #find the new connections
            delta = None
            if followerInfo != None:
                s = set(followerGroup)
                delta = [x for x in followerInfo if x not in s]
            if delta != []:
                print >> fOutputFile, "User ID:", friend, '\n', "New Followers:", delta
print "size of ID set of 0408:", len(newIdSet)