package modules

import akka.actor.ActorSystem
import net.codingwell.scalaguice.ScalaModule

class AppModule extends ScalaModule {
    override def configure(): Unit = {
        bind(classOf[ActorSystem]).toInstance(ActorSystem())
    }
}
