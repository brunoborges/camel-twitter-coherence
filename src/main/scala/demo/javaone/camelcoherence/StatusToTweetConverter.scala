package demo.javaone.camelcoherence

import org.apache.camel.Exchange
import org.apache.camel.Processor

import twitter4j.Status

object StatusToTweetConverter extends Processor {
  override def process(exchange: Exchange) = {
    val in = exchange.getIn()
    val status = in.getBody().asInstanceOf[Status]

    val file = status.getMediaEntities()(0).getMediaURL().getFile()
    val imgUrl = "https://pbs.twimg.com/media/%s".format(file)

    in.setBody(
      new Tweet(
        status.getUser().getScreenName(),
        status.getText(),
        imgUrl.asInstanceOf[String]))
  }
}