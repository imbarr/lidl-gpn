package circe.kafka

import java.nio.charset.StandardCharsets
import java.util

import io.circe._
import io.circe.parser._
import io.circe.syntax._
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

object Marshalling {

  implicit def jsonSerde[T: Decoder: Encoder]: Serde[T] = new Serde[T] {

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

    override def close(): Unit = {}

    override def serializer(): Serializer[T] = jsonSerializer

    override def deserializer(): Deserializer[T] = jsonDeserializer
  }

  private def jsonSerializer[T: Encoder]: Serializer[T] = new Serializer[T] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

    override def serialize(topic: String, data: T): Array[Byte] = {
      data.asJson.toString.getBytes
    }

    override def close(): Unit = {}
  }

  private def jsonDeserializer[T: Decoder]: Deserializer[T] = new Deserializer[T] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

    override def deserialize(topic: String, data: Array[Byte]): T = {
      val text = new String(data, StandardCharsets.UTF_8)

      parse(text).getOrElse(Json.Null).as[T] match {
        case Right(result) => result
        case Left(exception) => throw exception
      }
    }

    override def close(): Unit = {}
  }
}
