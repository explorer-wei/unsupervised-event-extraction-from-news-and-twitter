import json

def countOccurance(inputList):
    counts=dict()
    for aJasonBag in inputList:
        for element in aJasonBag["ids"]:
            if element in counts:
                counts[element] += 1
            else:
                counts[element] = 1

    metaCounts=dict()
    for count in counts.values():
        if count in metaCounts:
            metaCounts[count] += 1
        else:
            metaCounts[count] = 1
    return metaCounts


friendList=[]
followerList=[]
with open("user_info") as inputF:
    line=inputF.readline()
    while(line):
        line=inputF.readline()
#         These are the friends.
        friends=json.loads(line)
        friendList.append(friends)

        line=inputF.readline()
#         These are the followers.
        followers=json.loads(line)
        followerList.append(followers)


        line=inputF.readline()
#         end

print sorted(countOccurance(friendList).items())
print sorted(countOccurance(followerList).items())
