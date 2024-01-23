package spp.portfolio.manager.utilities.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonSerializationBuilders
{
    @FunctionalInterface
    public static interface JsonSerializerOperation<T>
    {
        public void apply(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException;
    }
    
    @FunctionalInterface
    public static interface JsonDeserializerOperation<T>
    {
        public T apply(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException;
    }

    @SuppressWarnings("serial")
    public static <T> StdSerializer<T> buildJsonSerializer(final Class<T> handledType, final JsonSerializerOperation<T> jsonSerializerOperation)
    {
        return 
                new StdSerializer<T>(handledType)
                {
                    @Override
                    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException
                    {
                        jsonSerializerOperation.apply(value, gen, provider);
                    }
                };
    }

    @SuppressWarnings("serial")
    public static <T> StdDeserializer<T> buildJsonDeserializer(final Class<T> handledType, final JsonDeserializerOperation<T> jsonDeserializerOperation) 
    {
        return
                new StdDeserializer<T>(handledType)
                {
                    @Override
                    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException
                    {
                        return jsonDeserializerOperation.apply(p, ctxt);
                    }
                };
    }
}
