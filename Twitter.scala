import org.elasticsearch.spark._
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.{ SparkConf, SparkContext }
import twitter4j.Status
import twitter4j.FilterQuery
import org.apache.log4j._
import edu.stanford.nlp.sentiment._
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{ Annotation, StanfordCoreNLP }
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import java.util.Properties
import scala.collection.JavaConversions._
import edu.stanford.nlp.util.logging.RedwoodConfiguration
import scala.collection.mutable.ListBuffer
import com.google.common.base.CharMatcher

val nlpProps = {
    val props = new Properties()
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment")
    props
  }

  def detectSentiment(message: String): String = {

  
   RedwoodConfiguration.current().clear().apply();

    val pipeline = new StanfordCoreNLP(nlpProps)

    val annotation = pipeline.process(message)
    var sentiments: ListBuffer[Double] = ListBuffer()
    var sizes: ListBuffer[Int] = ListBuffer()

    var longest = 0
    var mainSentiment = 0

    for (sentence <- annotation.get(classOf[CoreAnnotations.SentencesAnnotation])) {
      val tree = sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
      val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
      val partText = sentence.toString

      if (partText.length() > longest) {
        mainSentiment = sentiment
        longest = partText.length()
      }

      sentiments += sentiment.toDouble
      sizes += partText.length

    }

    val averageSentiment:Int = {
      if(sentiments.size > 0) (sentiments.sum / sentiments.size).toInt
      else -1
    }
    
    var rating=""
    if (averageSentiment < 2.0)  rating+="NEGATIVE"
    else if(averageSentiment < 3.0) rating+="NEUTRAL"
    else if(averageSentiment < 4.0) rating+="POSITIVE"

    rating
}  

    
    //Setting twitter4j properties
	System.setProperty("twitter4j.oauth.consumerKey", "hO73NTZg4uzAyJZmsAzNKPxNz")
    System.setProperty("twitter4j.oauth.consumerSecret", "iPq0MRFKcundVko0Yo1vsuAGRuNNeO3M7BIpgQx26E6JUN13m9")
    System.setProperty("twitter4j.oauth.accessToken", "782639803737120768-25gaXVXedFSbYnYm1DjopGUJRtydbNW")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "5vWFwbLtIe8gOJUWkA1q3YZbaLw0VapcwGH6bTFTMwHRG")
	

     @transient val ssc = new StreamingContext(sc, Seconds(30))
  
     //Creating a twitter stream
	 @transient  val stream = TwitterUtils.createStream(ssc, None)

     //Filtering tweets by removing emojis and special characters. Also removing tweets from all languages other than English.
	 @transient val tweets = stream.filter { status =>
        status.getLang() == "en" && CharMatcher.ASCII.matchesAllOf(status.getText)
}

     @transient val hashTagStream = tweets.map(x=>(x.getText))
     tweets.print()  
	
     tweets.foreachRDD{(rdd, time) =>
     rdd.map(t => {
     	import com.koddi.geocoder.Geocoder

	// Initialize the client for Google Geo API
	val client = Geocoder.create()

	// Lookup a location with a formatted address string
	// Returns a Seq[Result]
	val results = client.lookup(t.getUser.getLocation)
	val location1 = results.head.geometry.location


        Map(
			"location" -> Option(location1).map(geo => { s"${geo.latitude},${geo.longitude}" }),
			"text" -> t.getText,
            "HashTags" -> t.getText.split(" ").filter(_.startsWith("#")).mkString(" "),   
			"sentiment" -> detectSentiment(t.getText)
         )
       }).saveToEs("shark/tweets")
	   //saveToEs saves the data to elasticsearch using shark as the index
} 
    // Let's start the stream
    ssc.start()

    // Let's let the stream go on forever
   ssc.awaitTermination()
