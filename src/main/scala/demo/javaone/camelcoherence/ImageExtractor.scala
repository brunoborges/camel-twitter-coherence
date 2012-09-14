package demo.javaone.camelcoherence

import org.apache.camel.Exchange
import org.apache.camel.Processor

import twitter4j.Status

object ImageExtractor extends Processor {

  override def process(exchange: Exchange) {
    val status = exchange.getIn().getBody(classOf[Status]);
    val mediaEntities = status.getMediaEntities();

    if (mediaEntities != null && mediaEntities.length >= 0) {
      val mediaEntity = mediaEntities(0);

      exchange.getIn().setBody(
        new Tweet(
          status.getUser().getScreenName(),
          status.getText(),
          mediaEntity.getMediaURL().toString()));

      exchange.getIn().setHeader("UNIQUE_IMAGE_URL", mediaEntity.getMediaURL().toString());
    }
  }

}