import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import modules.AppModule
import net.codingwell.scalaguice.InjectorExtensions._
import routes.Routes

import scala.io.StdIn

object Main extends App {
  val injector = Guice.createInjector(new AppModule)

  implicit val system = injector.instance[ActorSystem]
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val routes = injector.instance[Routes]

  val binding = Http().bindAndHandle(routes.withLogging, "0.0.0.0", 9008)

  println("Press RETURN to stop...")
  StdIn.readLine()
  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
