import json

userIDs = []
with open('user_list_complete.txt') as inputF:
    for line in inputF:
        if '\'username\'' in line:
            userIDs.append(line[15:-2])
print "\n".join(userIDs)
