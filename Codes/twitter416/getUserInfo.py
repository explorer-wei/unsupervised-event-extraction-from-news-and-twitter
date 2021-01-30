from twitterOauth import *
import getopt
import sys
import json
import time

# print json.dumps(json.loads("some json string here"), sort_keys=True, indent=4);

keyIndex=0;

def getAllPages(output, userName,count,wantFriends=True):
    target="friends" if wantFriends else "followers"
    cursor=-1
    output.append(target)
    counter = 0
    global keyIndex
    while cursor!=0:

        restart=False
        url="https://api.twitter.com/1.1/"+target+"/ids.json?"+"cursor="+str(cursor)+"&screen_name="+userName+"&count=5000"
        content = oauth_req(url,keyIndex)
        p = json.loads(content)
        if "errors" in p:
            for error in  p["errors"]:
                print error
                if (error["code"]==88):
                    keyIndex = keyIndex + 1
                    print "CHANGING KEYINDEX to", keyIndex
                    if (keyIndex == NUM_KEYS):
                        keyIndex = 0
                        print "\rrestart request in 300 seconds"
                        time.sleep(300)
                    restart=True
                else:
                    return
        if not restart:
            try:
                counter+=1
                output.append(content)
                cursor=p["next_cursor"]
                sys.stdout.write( "\r"+"Part "+target+" "+str(counter)+" of "+str(count)+"/"+str(length)+" done")
                sys.stdout.flush()
                time.sleep(0.2)
            except KeyError:
                print content
                return

        

inputF="user_list"
outputF="user_info"
start=0
limit=sys.maxint
try:
    opts, args = getopt.getopt(sys.argv[1:], "o:i:s:l:", ["output=", "input=","start=","limit="])
except getopt.GetoptError as err:
        # print help information and exit:
    print(err) # will print something like "option -a not recognized"
    usage()
    sys.exit(2)
for o, a in opts:
    if o == "-o":
        outputF=a
    elif o in ("-i"):
        inputF=a
    elif o in ("-s"):
        start=int(a)
    elif o in ("-l"):
        limit=int(a)
    else:
        assert False, "unhandled option"
    # ...


outputF+="_"+str(start)+"-"+str(limit)


inputs = []
with open(inputF) as f:
    inputs = f.readlines();
count=0
length=len(inputs)
skippedUsers=set()
with open('escapeList') as escapeList:
    skippedUsers = set(escapeList.readlines())

with open(outputF,"w") as outputF:
    for userName in inputs:
        if(count>=start and count<limit):
            if (userName and userName not in skippedUsers):
                contents=[]
                contents.append(str(count))
                userName = userName.strip()
                contents.append(oauth_req("https://api.twitter.com/1.1/users/show.json?screen_name="+userName,0));
                getAllPages(contents,userName,count,True)
                getAllPages(contents,userName,count,False)
                outputF.write("\n".join(contents))
                outputF.write("\n")
                outputF.flush()
                print "\r"+str(count)," done                                       "

        count+=1
