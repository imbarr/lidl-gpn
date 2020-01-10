package routes

import akka.http.scaladsl.model.ws.{TextMessage, UpgradeToWebSocket}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Sink
import circe.akka.Marshalling._
import io.circe.Printer
import io.circe.generic.auto._
import io.circe.syntax._
import javax.inject.{Singleton, Inject}
import kafka.JournalService
import models.Event

@Singleton
class Routes @Inject()(journalService: JournalService) {

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
      headerValueByType[UpgradeToWebSocket]() { upgrade =>
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
