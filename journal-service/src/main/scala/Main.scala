import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import modules.AppModule
import net.codingwell.scalaguice.InjectorExtensions._
import routes.Routes

import scala.io.StdIn

object Main extends App {
  implicit val system = ActorSystem("journal-service")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val injector = Guice.createInjector(new AppModule)
  val routes = injector.instance[Routes]

  val binding = Http().bindAndHandle(routes.root, "localhost", 9008)

  println("Press RETURN to stop...")
  StdIn.readLine()
  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
