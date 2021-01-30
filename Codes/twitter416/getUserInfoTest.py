from twitterOauth import *
import sys
import json

userName="CadyWang"

print json.dumps(json.loads(oauth_req("https://api.twitter.com/1.1/application/rate_limit_status.json?resources=help,users,search,statuses")), indent=4)
