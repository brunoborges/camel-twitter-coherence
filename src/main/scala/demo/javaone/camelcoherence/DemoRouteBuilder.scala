package demo.javaone.camelcoherence

import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.component.twitter.TwitterComponent
import com.oracle.coherence.components.camel.CacheConstants

class DemoRouteBuilder extends RouteBuilder with ConfigureTwitterComponent {

  Statistics.keywords = configProperties.getProperty("twitter.searchTerm")
  Statistics.keywords = "photo"

  /*
  val tc = new TwitterComponent()
  tc.setAccessToken("14566379-pXIfEE24ByIwOUq1jHwR2CubGlDUIN0q9OWsc9iU")
  tc.setAccessTokenSecret("ZpEcg1uyNvZdUA1gFAjqs7Ld22DjjExTOJHNtddU")
  tc.setConsumerKey("6KpGgI631s4RanmOB3qbg")
  tc.setConsumerSecret("pYDxMF69eUM49PCenQz374x5HeEY8I9Tx75jFvL3U")
  */

  //getContext.addComponent("twitter", tc)

  "twitter://streaming/sample?type=event" ==> {
	  to("log:output")
	  setHeader(CacheConstants.CACHE_OPERATION, CacheConstants.CACHE_OPERATION_ADD)
	  setHeader(CacheConstants.CACHE_KEY, "lastTweet")
	  to("coherence:tweets")
  }
/*  
  "twitter://streaming/sample?type=event" +
  "&accessToken=14566379-pXIfEE24ByIwOUq1jHwR2CubGlDUIN0q9OWsc9iU" +
  "&accessTokenSecret=ZpEcg1uyNvZdUA1gFAjqs7Ld22DjjExTOJHNtddU" +
  "&consumerKey=6KpGgI631s4RanmOB3qbg" +
  "&consumerSecret=pYDxMF69eUM49PCenQz374x5HeEY8I9Tx75jFvL3U" to "log:output"
*/
}
