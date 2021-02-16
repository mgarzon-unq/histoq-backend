package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.opencv.core.Scalar;

import java.io.IOException;

public class ScalarJsonSerializer extends StdSerializer<Scalar> {

    public ScalarJsonSerializer() {
        this(null);
    }

    public ScalarJsonSerializer(Class<Scalar> t) {
        super(t);
    }

    @Override
    public void serialize(
            Scalar value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("val");
        jgen.writeArray(value.val, 0, value.val.length);
        jgen.writeEndObject();
    }
}

