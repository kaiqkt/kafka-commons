import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

inline fun <reified T> onMessage(properties: Properties, topic: String, noinline service: (T) -> Unit) =
    onMessage(properties, topic, T::class.java, service)

fun <T> onMessage(
    properties: Properties,
    topic: String,
    clazz: Class<T>,
    service: (T) -> Unit
) {
    val log: Logger = LoggerFactory.getLogger(clazz)

    createConsumer(properties).apply {
        subscribe(listOf(topic))

        while (true) {
            val records = poll(Duration.ofSeconds(0))

            records.iterator().forEach {
                val event = ObjectMapper().readValue(it.value(), clazz)
                service(event)
                log.info("Consumed event {} from topic {}", it.key(), it.topic())
            }
        }
    }
}

private fun createConsumer(properties: Properties): Consumer<String, String> = KafkaConsumer(properties)