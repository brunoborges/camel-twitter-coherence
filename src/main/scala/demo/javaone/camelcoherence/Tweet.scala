package demo.javaone.camelcoherence

import scala.reflect.BeanProperty

class Tweet(
  @BeanProperty var name: String,
  @BeanProperty var text: String,
  @BeanProperty var url: String
)