# -*- coding: utf-8 -*-
import tweepy
from credentials import *


def tweetFields(tweet):
  print("\n\nTweet id: " + str(tweet.id))
  print("Tweet user: " + str(tweet.user.name))
  print("Tweet user.followers: " + str(tweet.user.followers_count))
  print("Tweet user.following: " + str(tweet.user.friends_count))
  print("Tweet user.tweets: " + str(tweet.user.statuses_count))
  print("Tweet user.favourites: " + str(tweet.user.favourites_count))
  print("Tweet user.Timezone: " + str(tweet.user.time_zone))
  print("Tweet user.Location: " + str(tweet.user.location))


auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)

api = tweepy.API(auth)

tweetsPrev = api.search("#Salamanca", count=100, since="2017-03-23",
                        until="2017-03-28", lang="es", show_user=True)

print("100 Tweets:")
i = 1
for tweet in tweetsPrev:
  print(str(i) + '\t' + tweet.text)
  i += 1

totalTweets = tweetsPrev[:]
for j in range(4):
  tweets = api.search("#Salamanca", count=100, since="2017-03-23",
                      until="2017-03-28", lang="es", show_user=True, max_id=tweetsPrev[99].id - 1)

  print("\n\nMore Tweets:")
  for tweet in tweets:
    print(str(i) + '\t' + tweet.text)
    i += 1
  tweetsPrev = tweets
  totalTweets.extend(tweets)


print("\n\nTotal Tweets: " + str(len(totalTweets)))

tweetFields(tweets[99])

print("\n\nTweet detail:")
print(tweets[99])

output = open(r'tweetDetail.txt', 'w')
output.write(str(tweets[99]))
output.flush()
output.close()

'''
try:
  for tweet in tweepy.Cursor(api.search, q="#Salamanca", lang="es").items():
    print(tweet.text)
except Exception as e:
  print(e.message[0])
finally:
  pass
'''


def tweetFields(tweet):
  print("\n\nTweet id: " + str(tweet.id))
  print("Tweet user: " + str(tweet.user.name))
  print("Tweet user.followers: " + str(tweet.user.followers_count))
  print("Tweet user.following: " + str(tweet.user.friends_count))
  print("Tweet user.tweets: " + str(tweet.user.statuses_count))
  print("Tweet user.favourites: " + str(tweet.user.favourites_count))
  print("Tweet user.Timezone: " + str(tweet.user.time_zone))
  print("Tweet user.Location: " + str(tweet.user.location))
