package kafka

import java.util.{Properties, UUID}

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.effect.IO
import circe.kafka.Marshalling._
import circe.kafka.SimpleTimestampExtractor
import io.circe.generic.auto._
import models.Event
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology.AutoOffsetReset
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.Consumed
import org.apache.kafka.streams.state.Stores


class JournalService {
  private val journal = "journal"

  private val properties = new Properties()
  properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "journal-service")
  properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092")

  private val consumed = Consumed.`with`[UUID, Event](
    SimpleTimestampExtractor[Event](_.date),
    AutoOffsetReset.EARLIEST
  )

  private val builder = new StreamsBuilder()

  private val baseStore = Stores.inMemoryKeyValueStore(journal)
  private val store = Stores.keyValueStoreBuilder(baseStore, implicitly[Serde[UUID]], implicitly[Serde[Event]]).build()

  private val events = builder.stream(journal)(consumed)

  private val producer = new KafkaProducer(properties, implicitly[Serde[UUID]].serializer, implicitly[Serde[Event]].serializer)

  events.foreach((id, event) => store.put(id, event))

  def list: IO[Seq[Event]] = {
    ???
  }

  def getById(id: UUID): Option[Event] = {
    Option(store.get(id))
  }

  def stream: Source[Event, NotUsed] = {
    ???
  }

  def push(event: Event): IO[Unit] = {
    val record = new ProducerRecord(journal, 0, event.date.toEpochMilli, event.id, event)
    IO { producer.send(record).get() }
  }
}
