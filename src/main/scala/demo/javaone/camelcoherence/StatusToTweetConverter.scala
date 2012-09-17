package demo.javaone.camelcoherence

import org.apache.camel.Processor
import org.apache.camel.Exchange
import twitter4j.Status

object StatusToTweetConverter extends Processor {

  override def process(exchange: Exchange) = {
    val status = exchange.getIn().getBody().asInstanceOf[Status]
    val in = exchange.getIn()
    val usr = status.getUser()
    val imgUrl = in.getHeader("UNIQUE_IMAGE_URL")
    val tweet = new Tweet(
      usr.getScreenName(),
      status.getText(),
      imgUrl.asInstanceOf[String])

    exchange.getIn().setBody(tweet)
  }

}
