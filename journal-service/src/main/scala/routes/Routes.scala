package routes

import akka.event.Logging
import akka.http.scaladsl.model.ws.{TextMessage, UpgradeToWebSocket}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.scaladsl.Sink
import circe.akka.Marshalling._
import io.circe.Printer
import io.circe.generic.auto._
import io.circe.syntax._
import javax.inject.{Inject, Singleton}
import kafka.JournalService
import models.Event

@Singleton
class Routes @Inject()(journalService: JournalService) {

  lazy val withLogging = DebuggingDirectives.logRequestResult("REST", Logging.InfoLevel)(root)

  lazy val root: Route = {
    list ~
    stream ~
    getById ~
    push
  }

  private def list: Route = {
    path("events") {
      get {
        complete(journalService.list)
      }
    }
  }

  private def getById: Route = {
    path("event" / JavaUUID) { id =>
      get {
        complete(journalService.getById(id))
      }
    }
  }

  private def stream: Route = {
    path("events" / "stream") {
      extractUpgradeToWebSocket { upgrade =>
        val source = journalService.stream
          .map(_.asJson.printWith(Printer.noSpaces))
          .map(TextMessage(_))
        val answer = upgrade.handleMessagesWithSinkSource(Sink.ignore, source)
        complete(answer)
      }
    }
  }

  private def push: Route = {
    path("events") {
      post {
        entity(as[Event]) { event =>
          complete(journalService.push(event))
        }
      }
    }
  }

}
