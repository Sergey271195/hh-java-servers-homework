package application.serilalization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class DateTimeSerializer extends StdSerializer<LocalDateTime> {

    public DateTimeSerializer() { this(null); }

    protected DateTimeSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(LocalDateTime date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(date.toString());
    }
}
