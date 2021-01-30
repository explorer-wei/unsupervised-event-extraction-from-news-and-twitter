import oauth2 as oauth

API_KEY=["Wp2oUJPpzzg5csN4nRFyamUQq","5s7hIiGrGkjWE2rMOPqEt08fB","rSDxUqjxmepNFwkHYZ7duiJ9y","7wgwZremUaJ00tUvRUQkUA","EvTBIaUvNjCpIRbb0fxAow","w01GmQ63EdTvTY0xnVdaQ","Zk4uWb7iebMbT4jhzdgyiQ"]
API_SECRET=["qHlsTwMcf4ngZw8G0m3n1d6sCVQX9Lxkq4Og3p03cGH41GGV7l","ZfnoK9ufuVN71nXF8vM4lYgb7CWXaTGQrSn9NnCjxw3q63dUBb","2pB2rXE6e8KJLReUbZJvTfs8SXXnaJqOaxmrtNzjvh216zs9e2","FtG5YOCrYsXDQXwbxe0jbqhvb63FyUx9BxjsdmEiCec","JWla1AputLai79tfthpvD5wNW2StA9lzbXsgiuk","1Rh2djBqw2UY2un89M6cIsTQQ2OxvYrW4rL7JhUmDc","bS4SwsgHRaE70BWwDCe4QMyMViR84hB8GqA9MaB81pM"]
ACCESS_KEY=["365864487-i5O8CAayolI2bRqi0h68GKhpF0VNB3TYd0JWHTOZ","365864487-LRcZ5MWYBKhvjZDA8cRgZ1SqLrI1mkcmCHB6K4J3","365864487-ixV8t21ZomstvWHVLGRPQO7MiA9yMaDtz7KiwXvg","365864487-dYH7d07KIXe3m7AbYaUWLUlSbb8Sm7nFCZ45oiBm","2407561982-q5a4DD80DIdQE1Rxni31ekoyamth5im56Ke18NG","2423001786-2sBH94EPygoqDEL9FDEkwvG69pe0rgGDwNKLqr6","2423001786-EPQo8ZBJJXCUBMpa6uuYIKaGGne9OnfOMgXANMn"]
ACCESS_SECRET=["Xa7o8Ibatpqy267NLwll8Adrcdp6Dv9IO3twy8o4EboQs","bdvCTIboLuOYxYKYgLYDQOX1yXxzi4mWxqpAwcifBa00D","L8bRYy5kIWLjluqVzefp1BG82ZGU1jO1pb5HmoHe7o","MBMX24glxZZSWIJrW3fez7xJVlkfsXbSnaNOXwGp24nxY","SVXQd2Y0iCOfZ8Ar8sXAPxQolGwRQISHwJPthhFeG8kxs","JcPrDUHg6RU2MmvNF7oN6vWTpO7tneJ2y8AhtXuuVcCH6","S4jTSRnbW3CQiqW5X8QohahrAWE5PQgoVmk6X9suY5Pq4"]

NUM_KEYS=len(API_KEY)

def oauth_req(url, keyIndex,  http_method="GET", post_body=None,
        http_headers=None):
    consumer = oauth.Consumer(key=API_KEY[keyIndex], secret=API_SECRET[keyIndex])
    token = oauth.Token(key=ACCESS_KEY[keyIndex], secret=ACCESS_SECRET[keyIndex])
    client = oauth.Client(consumer, token)
 
    resp, content = client.request(
        url,
        method=http_method,
        body="",
        headers=http_headers,
    )
    return content
