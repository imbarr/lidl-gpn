package circe.kafka

import java.time.Instant

import org.apache.kafka.streams.processor.TimestampExtractor

import scala.reflect.ClassTag

object SimpleTimestampExtractor {

  def apply[T: ClassTag](extract: T => Instant): TimestampExtractor = {
    (record, _) => record.value() match {
      case value: T => extract(value).toEpochMilli
      case _ => throw new IllegalArgumentException("Failed to extract timestamp")
    }
  }

}
