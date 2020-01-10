package circe.akka

import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentType, ContentTypeRange, HttpEntity}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import io.circe.{Decoder, Encoder, Json, Printer, jawn}
import akka.util.ByteString
import cats.effect.IO

object Marshalling {

  implicit def jsonMarshaller(implicit printer: Printer = Printer.noSpaces): ToEntityMarshaller[Json] = {
    Marshaller.oneOf(`application/json`) { mediaType =>
      Marshaller.withFixedContentType(ContentType(mediaType)) { json =>
        val bytes = ByteString(printer.printToByteBuffer(json, mediaType.charset.nioCharset))
        HttpEntity(mediaType, bytes)
      }
    }
  }

  implicit def marshaller[A: Encoder](implicit printer: Printer = Printer.noSpaces): ToEntityMarshaller[A] = {
    jsonMarshaller.compose(Encoder[A].apply)
  }

  implicit val jsonUnmarshaller: FromEntityUnmarshaller[Json] = {
    Unmarshaller.byteStringUnmarshaller.forContentTypes(ContentTypeRange(`application/json`)).map {
      case ByteString.empty => throw Unmarshaller.NoContentException
      case data => jawn.parseByteBuffer(data.asByteBuffer).fold(throw _, identity)
    }
  }

  implicit def unmarshaller[A: Decoder]: FromEntityUnmarshaller[A] = {
    jsonUnmarshaller
      .map(Decoder[A].decodeJson)
      .map(_.fold(throw _, identity))
  }

  implicit def ioMarshaller[A: ToEntityMarshaller]: ToEntityMarshaller[IO[A]] = {
    implicitly[ToEntityMarshaller[A]].compose(_.unsafeRunSync())
  }

}
