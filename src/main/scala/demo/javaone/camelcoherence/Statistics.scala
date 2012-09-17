package demo.javaone.camelcoherence

import java.util.Calendar

import scala.reflect.BeanProperty

import org.apache.camel.Exchange
import org.apache.camel.Processor

import twitter4j.Status

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

}

object StatisticsProcessor extends Processor {
  override def process(exchange: Exchange) = {
    val status = exchange.getIn().getBody().asInstanceOf[Status]
    if (status.getMediaEntities() == null) {
      Statistics.tweetCount = Statistics.tweetCount + 1
    } else {
      Statistics.tweetCount = Statistics.tweetCount + 1
      Statistics.imageCount = Statistics.imageCount + 1
    }
  }
}
