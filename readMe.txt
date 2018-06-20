The below command was used to run this code

Here /usr/local/spark1 is the installation directory of spark 1.5.2

stanford-english-corenlp-2018-02-27-models.jar is needed to run this code

/usr/local/spark1/bin/spark-shell --master local[2] -i Twitter.scala --packages edu.stanford.nlp:stanford-corenlp:3.9.1,org.apache.spark:spark-streaming-twitter_2.11:1.6.3,com.google.guava:guava:11.0.2,org.elasticsearch:elasticsearch-spark-13_2.10:5.4.0,com.koddi:geocoder_2.11:1.0.2 --jars stanford-english-corenlp-2018-02-27-models.jar --conf spark.es.resource=index/type
