package demo.javaone.camelcoherence

import java.util.Calendar
import scala.reflect.BeanProperty
import org.apache.camel.Exchange
import twitter4j.Status
import org.apache.camel.Processor

object Statistics {

  @BeanProperty val startedOn = Calendar.getInstance().getTime()
  @BeanProperty var tweetCount = 0
  @BeanProperty var imageCount = 0
  @BeanProperty var keywords: String = _

  def increaseCounts() = {
    tweetCount += 1
    imageCount += 1
  }

  def increaseTweets() = tweetCount += 1

  def clear() = {
    tweetCount = 0
    imageCount = 0
  }

  protected object StatisticsProcessor extends Processor {
    override def process(exchange: Exchange) = {
      if (exchange.getIn().getBody().isInstanceOf[Status]) {
        tweetCount += 1
      } else if (exchange.getIn().getBody().isInstanceOf[Tweet]) {
        tweetCount += 1
        imageCount += 1
      }
    }
  }

}
