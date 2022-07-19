import com.fasterxml.jackson.annotation.JsonProperty

data class Event<T>(
    @JsonProperty("correlationId")
    val correlationId: String,
    @JsonProperty("payload")
    val payload: T
)