package kafka

import java.util.UUID

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import cats.effect.IO
import circe.kafka.Marshalling._
import io.circe.generic.auto._
import javax.inject.{Inject, Singleton}
import models.Event
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.Serde

@Singleton
class JournalService @Inject()(system: ActorSystem) {
  val journal = "journal"

  val partition = new TopicPartition(journal, 0)

  val consumerSettings = ConsumerSettings(system, implicitly[Serde[UUID]].deserializer, implicitly[Serde[Event]].deserializer)
      .withBootstrapServers("kafka:9092")

  val consumer = Consumer.plainSource(consumerSettings, Subscriptions.assignment(partition)).map(_.value)

  def list: IO[Seq[Event]] = {
    ???
  }

  def getById(id: UUID): Option[Event] = {
    ???
  }

  def stream: Source[Event, Consumer.Control] = {
    consumer
  }

  def push(event: Event): IO[Unit] = {
    ???
  }
}
