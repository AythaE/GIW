# -*- coding: utf-8 -*-
import tweepy
from credentials import *

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)

api = tweepy.API(auth)

tweets = api.search("#Salamanca", lang="es", show_user=True)

print(tweets[1])


# for tweet in tweets:
# print(tweet)


try:
  for tweet in tweepy.Cursor(api.search, q="#Salamanca", page="1", lang="es", show_user=True).items():
    print(tweet.text)
except Exception as e:
  print(e.message[0])
finally:
  pass
