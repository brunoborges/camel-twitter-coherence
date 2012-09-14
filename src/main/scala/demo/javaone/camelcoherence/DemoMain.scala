package demo.javaone.camelcoherence

import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport

/**
 * A Main to run Camel with MyRouteBuilder
 */
object DemoMain extends RouteBuilderSupport {

  def main(args: Array[String]) {
    val main = new Main()
    main.enableHangupSupport();
    main.addRouteBuilder(new DemoRouteBuilder())
    main.run();
  }

}
