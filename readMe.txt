Real-Time Sentiment Analysis of Tweets using Scala for Spark Streaming, Stanford CoreNLP for sentiment analyzation and ElasticSearch & Kibana for visualization. Collect tweets in real time. Filter out special characters and emoji symbols. Restrict the tweets to English language and perform sentiment analysis using Stanford CoreNLP. Use Google Geo API to get the longitude and latitude of the location from where the tweet is posted. Visualize all this information in Kibana.	

You need to install elasticsearch 2.0.0 and Kibana 4.2.0 to run this code. Also you need to download stanford-english-corenlp-2018-02-27-models.jar as it implements Sentimental analysis.

The below command was used to run this code

/usr/local/spark1/bin/spark-shell --master local[2] -i Twitter.scala --packages edu.stanford.nlp:stanford-corenlp:3.9.1,org.apache.spark:spark-streaming-twitter_2.11:1.6.3,com.google.guava:guava:11.0.2,org.elasticsearch:elasticsearch-spark-13_2.10:5.4.0,com.koddi:geocoder_2.11:1.0.2 --jars stanford-english-corenlp-2018-02-27-models.jar --conf spark.es.resource=index/type

Here /usr/local/spark1 is the installation directory of spark 1.5.2

edu.stanford.nlp:stanford-corenlp:3.9.1,org.apache.spark:spark-streaming-twitter_2.11:1.6.3,com.google.guava:guava:11.0.2,org.elasticsearch:elasticsearch-spark-13_2.10:5.4.0,com.koddi:geocoder_2.11:1.0.2 are the different packages that are used to compile this code.

After you run this code. You need to start elasticsearch and kibana. After that you should open Kibana GUI and setup the index. Then you can create various visualizations in Kibana. I had created HeatMap and GeoMap visualizations.
