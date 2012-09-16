package demo.javaone.camelcoherence

import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.component.twitter.TwitterComponent
import com.oracle.coherence.components.camel.CacheConstants
import twitter4j.Status
import org.apache.camel.processor.aggregate.UseLatestAggregationStrategy

class DemoRouteBuilder extends RouteBuilder with ConfigureTwitterComponent {

  Statistics.keywords = configProperties.getProperty("twitter.searchTerm")

  "twitter://streaming/sample?type=event&keywords=batman" ==> {
    when(_.in.asInstanceOf[Status].getMediaEntities() != null) {
      setHeader("UNIQUE_IMAGE_URL", _.in.asInstanceOf[Status].getMediaEntities()(0).getMediaURL().toString())
      to("direct:tweetsWithImage")
    }
  }

  "direct:tweetsWithImage" ==> {
    to("direct:checkCache")
    when(_.getIn().getHeader(CacheConstants.CACHE_ELEMENT_WAS_FOUND) == null) {
      to("direct:storeOnCache")
      to("direct:aggregate")
    }
  }

  "direct:checkCache" ==> {
    setHeader(CacheConstants.CACHE_OPERATION, CacheConstants.CACHE_OPERATION_CHECK)
    setHeader(CacheConstants.CACHE_KEY, _.header("UNIQUE_IMAGE_URL"))
    to("coherence:tweets")
  }

  "direct:storeOnCache" ==> {
    setHeader(CacheConstants.CACHE_OPERATION, CacheConstants.CACHE_OPERATION_ADD)
    setHeader(CacheConstants.CACHE_KEY, _.header("UNIQUE_IMAGE_URL"))
    to("coherence:tweets")
  }

  "direct:aggregate" ==> {
    aggregate(_.in.asInstanceOf[Status].getId() > 0, new UseLatestAggregationStrategy()).completionInterval(1000).to("direct:publish")
  }
  
  "direct:publish" ==> {
    to("log:output")
  }
}
