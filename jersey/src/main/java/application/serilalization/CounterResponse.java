package application.serilalization;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public class CounterResponse {

    private Long value;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime date;

    public CounterResponse() {}

    public CounterResponse(Long value, LocalDateTime date) {
        this.value = value;
        this.date = date;
    }

    public Long getValue() { return value; }

    public void setValue(Long value) { this.value = value; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }


}
