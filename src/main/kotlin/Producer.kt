import com.fasterxml.jackson.databind.ObjectMapper
import io.azam.ulidj.ULID
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

inline fun <reified T> emit(properties: Properties, topic: String, event: Event<T>) =
    emit(properties, topic, event, T::class.java)

fun <T> emit(properties: Properties, topic: String, event: Event<T>, clazz: Class<T>) {
    val log: Logger = LoggerFactory.getLogger(clazz)

    val record = getProducerRecord(event, event.correlationId, topic, clazz)

    createProducer(properties).apply {
        send(record) { data, ex ->
            if (ex != null) {
                log.error("Error to send event {} error: {}", record.key(), ex.message)
                return@send
            }
            log.info(
                "Send event {} to topic {} at timestamp {}",
                record.key(),
                data.topic(),
                data.timestamp()
            )
        }.get()
    }
}

private fun <T> getProducerRecord(
    event: Event<T>,
    correlationId: String?,
    topic: String,
    clazz: Class<T>
): ProducerRecord<String, String> {
    val serializedEvent = ObjectMapper().writeValueAsString(event)
    val eventTypeHeader = RecordHeader(
        "eventType",
        ObjectMapper().writeValueAsBytes(clazz)
    )
    val headers = listOf<Header>(eventTypeHeader)

    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
    return ProducerRecord(
        topic,
        null,
        correlationId ?: ULID.random(),
        serializedEvent,
        headers
    )
}

private fun createProducer(properties: Properties) = KafkaProducer<String, String>(properties)
