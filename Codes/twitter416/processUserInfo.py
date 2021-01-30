import os
import re 
import json
import sys
inputFiles=[]
with open("/share/user_info_complete_merged-"+sys.argv[1],"w") as outputFile:
    for path,dirs,files in os.walk("."):
        for f in files:
            if re.match(r"user_info_complete-"+sys.argv[1]+"_\d+-\d" ,f):
                with open(f) as inputFile:
                    flag=0
                    temp=None
                    counter=0
                    for line in inputFile:
                        counter+=1
                        line=line.strip()
                        if re.match('^[0-9]+',line):
                            flag=0
                            if temp:
                                outputFile.write(json.dumps(temp))
                                outputFile.write('\n')
                            temp=None

                        elif 'friends'==line:
                            flag=1
                        elif 'followers'==line:
                            flag=2
                        else:
                            current = json.loads(line)
                            if flag==0:
                                temp=current
                                temp['friends']=[]
                                temp['followers']=[]
                            elif flag==1:
                                if 'ids' in current:
                                    temp['friends'].extend(current['ids'])
                            else:
                                if 'ids' in current:
                                    temp['followers'].extend(current['ids'])

                    outputFile.write(json.dumps(temp))
                    outputFile.write('\n')
                                    







