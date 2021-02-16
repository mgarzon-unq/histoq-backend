package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.opencv.core.Scalar;

import java.io.IOException;

public class ScalarJsonDeserializer extends StdDeserializer<Scalar> {

    public ScalarJsonDeserializer() {
        this(null);
    }

    protected ScalarJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Scalar deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ArrayNode valNode = (ArrayNode)jsonParser.getCodec().readTree(jsonParser).get("val");
        return new Scalar(valNode.get(0).asDouble(),valNode.get(1).asDouble(),valNode.get(2).asDouble(),valNode.get(3).asDouble());
    }
}

