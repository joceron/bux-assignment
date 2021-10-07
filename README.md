# bux-assignment

## Summary
In this assignment, we would like to ask you to read some real-time trading data (Tickers) from a public websocket, and 
to do some transformations with it.

You can use local Kafka (or other external systems, if you need them). As an extra task, it would be amazing if you 
could include a docker-compose file to run the dependencies for your application, but that is not necessary - the code 
is the main focus. We ask you to use Scala to complete the task. You can use any frameworks or libraries.

It would be great if you could share the results through Github. Create a new public repo, and send us the link.

The first assignment can take around 1-3 hours, depending on your experience level. The additional advanced task can 
take a bit longer. There is no countdown, this is just for you to plan your time. Do not worry, if you don't have time 
to finish the tasks completely. Our goal is just to see how you code and think about real-life problems. We will walk 
through your code together in a follow-up interview, and you will be able to share your plans for implementing any 
missing parts of the code then.

## Task details
- Subscribe to the data from the public websocket at https://docs.kucoin.com/#all-symbols-ticker. This websocket should 
  give you real-time data on all tickers from the Kucoin exchange, formatted as in the example given under the link. You
  can find more information about how to subscribe to the websocket in the docs under the link.
- Task 1: For each Ticker message received over the websocket, produce a record into a Kafka topic “tickers”. The key 
  for the record should be the ticker name (e.g. `BTC-USDT`). The value of the record should be JSON data formatted as 
  follows:
  ```
  {
    "prices": {
      "bid": <bestBid>,
      "ask": <bestAsk>
    },
    "size": <size>
  }
  ```
- Task 2 - Advanced: Reading the data from the kafka topic you have created in task 1, create a new “price alert” topic 
  in kafka. The end goal is to send a message to the topic when, upon receiving a new message from the kafka topic, for 
  a certain ticker, the price difference in the last 24h is more than 5%. For example, if we receive a Ticker for 
  BTC-EUR with price 35000 and we know that 24 hours ago the price was 50000, we should send out an alert. This task is 
  a bit open-ended, and the implementation is up to you. Choose the key and value format of kafka records as you see 
  best. Feel free to add other external systems, or more kafka topics to your solution if you feel that you need them.

### Libraries used

- http4s: HTTP for Scala with streams. It's the one I use at my current job for all the HTTP requests to third-parties,
so I feel confident with it.
- Cats: it's just convenient to have. Also, needed for http4s.
- SLF4J: http4s needs it. Also, convenient to have in general
- Circe: JSON library build on top of Cats. For encoding/decoding HTTP requests

### Things to improve

- I import SLF4J library, but I'm actually not using it myself, and I'm logging everything with printlns on the console