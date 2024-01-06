package spp.portfolio.constituents.json;

import static spp.portfolio.model.json.JsonSerializationBuilders.buildJsonDeserializer;
import static spp.portfolio.model.json.JsonSerializationBuilders.buildJsonSerializer;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import spp.portfolio.constituents.rules.inmemory.BooleanOperator;
import spp.portfolio.constituents.rules.inmemory.ComparisonOperator;
import spp.portfolio.constituents.rules.inmemory.Currency;

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
    
    public static StdSerializer<Currency> getCurrencySerializer() 
    {
        return buildJsonSerializer(Currency.class
                                                , (value, gen, serializers)->{
                                                    if(Objects.nonNull(value))
                                                        gen.writeString(value.getCode());
                                                });
    };
    
    public static StdDeserializer<Currency> getCurrencyDeserializer() 
    {
        return buildJsonDeserializer(Currency.class
                                                    , (p, ctxt)->Optional.ofNullable(p.getValueAsString()).map(s->Currency.fromCode(s)).orElseThrow());
    }
    
    public static StdSerializer<ComparisonOperator> getComparisonOperatorSerializer() 
    {
        return buildJsonSerializer(ComparisonOperator.class
                                                , (value, gen, serializers)->{
                                                    if(Objects.nonNull(value))
                                                        gen.writeString(value.getSymbol());
                                                });
    };
    
    public static StdDeserializer<ComparisonOperator> getComparisonOperatorDeserializer() 
    {
        return buildJsonDeserializer(ComparisonOperator.class
                                                    , (p, ctxt)->Optional.ofNullable(p.getValueAsString()).map(s->ComparisonOperator.getFromSymbol(s)).orElseThrow());
    }
    
    public static StdSerializer<BooleanOperator> getBooleanOperatorSerializer() 
    {
        return buildJsonSerializer(BooleanOperator.class
                                                , (value, gen, serializers)->{
                                                    if(Objects.nonNull(value))
                                                        gen.writeString(value.getSymbol());
                                                });
    }
    
    public static StdDeserializer<BooleanOperator> getBooleanOperatorDeserializer() 
    {
        return buildJsonDeserializer(BooleanOperator.class
                                                    , (p, ctxt)->Optional.ofNullable(p.getValueAsString()).map(s->BooleanOperator.getFromSymbol(s)).orElseThrow());
    }
}
