package spp.portfolio.model.json;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import spp.portfolio.model.definition.configuration.Currency;
import spp.portfolio.model.definition.configuration.rules.BooleanOperator;
import spp.portfolio.model.definition.configuration.rules.ComparisonOperator;

public class PortfolioConfigurationModule extends SimpleModule
{
    private static final long serialVersionUID = 6622174388343333706L;

    public PortfolioConfigurationModule()
    {
        super();
        addSerializer(Currency.class, getCurrencySerializer());
        addDeserializer(Currency.class, getCurrencyDeserializer());
        addSerializer(ComparisonOperator.class, getComparisonOperatorSerializer());
        addDeserializer(ComparisonOperator.class, getComparisonOperatorDeserializer());
        addSerializer(BooleanOperator.class, getBooleanOperatorSerializer());
        addDeserializer(BooleanOperator.class, getBooleanOperatorDeserializer());
    }
    
    @SuppressWarnings("serial")
    public static StdSerializer<Currency> getCurrencySerializer() 
    {
        return 
                new StdSerializer<Currency>(Currency.class) 
                {
                    @Override
                    public void serialize(Currency value, JsonGenerator gen, SerializerProvider serializers) throws IOException
                    {
                        if(Objects.nonNull(value))
                            gen.writeString(value.getCode());
                    }
                };
    };
    
    @SuppressWarnings("serial")
    public static StdDeserializer<Currency> getCurrencyDeserializer() 
    {
        return
                new StdDeserializer<Currency>(Currency.class)
                {
                    @Override
                    public Currency deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException
                    {
                        String symbol = p.getValueAsString();
                        return Optional.ofNullable(symbol).map(s->Currency.fromSymbol(s)).orElseThrow();
                    }
                };
    }
    
    @SuppressWarnings("serial")
    public static StdSerializer<ComparisonOperator> getComparisonOperatorSerializer() 
    {
        return 
                new StdSerializer<ComparisonOperator>(ComparisonOperator.class) 
                {
                    @Override
                    public void serialize(ComparisonOperator value, JsonGenerator gen, SerializerProvider serializers) throws IOException
                    {
                        if(Objects.nonNull(value))
                            gen.writeString(value.getSymbol());
                    }
                };
    };
    
    @SuppressWarnings("serial")
    public static StdDeserializer<ComparisonOperator> getComparisonOperatorDeserializer() 
    {
        return
                new StdDeserializer<ComparisonOperator>(ComparisonOperator.class)
                {
                    @Override
                    public ComparisonOperator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException
                    {
                        String symbol = p.getValueAsString();
                        return Optional.ofNullable(symbol).map(s->ComparisonOperator.getComparisonOperator(s)).orElseThrow();
                    }
                };
    }
    
    @SuppressWarnings("serial")
    public static StdSerializer<BooleanOperator> getBooleanOperatorSerializer() 
    {
        return 
                new StdSerializer<BooleanOperator>(BooleanOperator.class) 
                {
                    @Override
                    public void serialize(BooleanOperator value, JsonGenerator gen, SerializerProvider serializers) throws IOException
                    {
                        if(Objects.nonNull(value))
                            gen.writeString(value.getSymbol());
                    }
                };
    };
    
    @SuppressWarnings("serial")
    public static StdDeserializer<BooleanOperator> getBooleanOperatorDeserializer() 
    {
        return
                new StdDeserializer<BooleanOperator>(BooleanOperator.class)
                {
                    @Override
                    public BooleanOperator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException
                    {
                        String symbol = p.getValueAsString();
                        return Optional.ofNullable(symbol).map(s->BooleanOperator.getBooleanOperator(s)).orElseThrow();
                    }
                };
    }
}
