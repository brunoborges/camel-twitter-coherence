package demo.javaone.camelcoherence

import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.component.twitter.TwitterComponent
import java.io.InputStream
import java.util.Properties
import org.apache.camel.Endpoint

trait ConfigureTwitterComponent {
  self: RouteBuilder =>

  val configProperties = new Properties()
  val tc = new TwitterComponent()
  val is = getClass().getResourceAsStream("/app.properties")
  configProperties.load(is);

  val properties = Array("accessToken", "accessTokenSecret", "consumerKey", "consumerSecret")
  properties.foreach(p => {
    val key = "twitter." + p
    tc.getClass.getMethods.find(_.getName == "set" + Character.toUpperCase(p.charAt(0)) + p.drop(1)).get.invoke(tc, System.getProperty(key, configProperties.getProperty(key)).asInstanceOf[AnyRef])
  })

  override def onJavaBuilder(builder: org.apache.camel.builder.RouteBuilder) = {
    builder.getContext().addComponent("twitter", tc)
  }
}