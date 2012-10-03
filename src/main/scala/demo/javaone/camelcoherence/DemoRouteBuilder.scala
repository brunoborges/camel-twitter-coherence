package demo.javaone.camelcoherence

import org.apache.camel.model.dataformat.JsonDataFormat
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.processor.aggregate.UseLatestAggregationStrategy
import org.apache.camel.scala.dsl.builder.RouteBuilder
import com.oracle.coherence.components.camel.CacheConstants
import twitter4j.Status
import org.apache.camel.component.websocket.WebsocketConstants

class DemoRouteBuilder extends RouteBuilder with ConfigureComponents {

  val jsonFormat = new JsonDataFormat(JsonLibrary.Jackson)
  val lruImages = new FifoBlockingQueue[Tweet](12)
  val UNIQUE_IMAGE = "UNIQUE_IMAGE"
  Statistics.keywords = configProperties.getProperty("twitter.searchTerm")

  "twitter://streaming/filter?type=event&keywords=%s".format(Statistics.keywords) ==> {
    process(StatisticsProcessor)
    when(_.in.asInstanceOf[Status].getMediaEntities() != null) {
      setHeader(UNIQUE_IMAGE, _.in.asInstanceOf[Status].getMediaEntities()(0).getMediaURL().getFile())
      to("direct:tweetsWithImage")
    }
  }

  "direct:tweetsWithImage" ==> {
    setHeader(CacheConstants.CACHE_KEY, _.header(UNIQUE_IMAGE))
    setHeader(CacheConstants.CACHE_OPERATION, CacheConstants.CACHE_OPERATION_CHECK)
    to("coherence:tweets")
    when(_.getIn().getHeader(CacheConstants.CACHE_ELEMENT_WAS_FOUND) == null) {
      setHeader(CacheConstants.CACHE_OPERATION, CacheConstants.CACHE_OPERATION_ADD)
      setHeader(CacheConstants.CACHE_KEY, _.header(UNIQUE_IMAGE))
      to("coherence:tweets")
      aggregate(_.in.asInstanceOf[Status].getId() > 0, new UseLatestAggregationStrategy())
        .completionInterval(500)
        .to("direct:publish")
    }
  }

  "direct:publish" ==> {
    process(StatusToTweetConverter)
    process(exchange ⇒ {
      lruImages.offer(exchange.getIn().getBody().asInstanceOf[Tweet])
    })
    marshal(jsonFormat)
    to("websocket:0.0.0.0:8080/javaone/images?sendToAll=true")
  }

  "quartz:statistics?cron=* * * * * ?" ==> {
    setBody(Statistics)
    marshal(jsonFormat)
    to("websocket:0.0.0.0:8080/javaone/statistics?sendToAll=true")
  }

  "websocket:0.0.0.0:8080/javaone/sample" ==> {
    split(lruImages) {
      marshal(jsonFormat)
      to("websocket:0.0.0.0:8080/javaone/sample")
    }
  }

  "websocket:0.0.0.0:8080/javaone/retweet" ==> {
    process(exchange ⇒ {
      exchange.getIn().setBody("RT " + exchange.getIn().getBody())
    })
    to("twitter://timeline/user")
  }

}