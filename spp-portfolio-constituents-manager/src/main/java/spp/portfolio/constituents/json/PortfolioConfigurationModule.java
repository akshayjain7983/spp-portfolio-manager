package spp.portfolio.constituents.json;

import static spp.portfolio.manager.utilities.json.JsonSerializationBuilders.buildJsonDeserializer;
import static spp.portfolio.manager.utilities.json.JsonSerializationBuilders.buildJsonSerializer;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import spp.portfolio.constituents.rules.BooleanOperator;
import spp.portfolio.constituents.rules.Currency;
import spp.portfolio.constituents.rules.RelaxationCondition;
import spp.portfolio.constituents.rules.simple.ComparisonOperator;
import spp.portfolio.constituents.rules.simple.ExistOperator;
import spp.portfolio.manager.utilities.json.JsonSerializationBuilders;

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
        addKeySerializer(RelaxationCondition.class, getRelaxationConditionSerializer());
        addKeyDeserializer(RelaxationCondition.class, getRelaxationConditionKeyDeserializer());
        addSerializer(ExistOperator.class, getExistOperatorSerializer());
        addDeserializer(ExistOperator.class, getExistOperatorDeserializer());
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
    

    
    public static StdSerializer<RelaxationCondition> getRelaxationConditionSerializer() 
    {
        return buildJsonSerializer(RelaxationCondition.class
                                                , (value, gen, serializers)->{
                                                    if(Objects.nonNull(value))
                                                        gen.writeString(value.name());
                                                });
    }
    
    public static StdDeserializer<RelaxationCondition> getRelaxationConditionDeserializer() 
    {
        return buildJsonDeserializer(RelaxationCondition.class
                                                    , (p, ctxt)->Optional.ofNullable(p.getValueAsString()).map(s->RelaxationCondition.valueOf(s)).orElseThrow());
    }
    
    public static KeyDeserializer getRelaxationConditionKeyDeserializer()
    {
	return JsonSerializationBuilders.buildJsonMapKeyDeserializer(RelaxationCondition.class
													, (k, ctx)->Optional.ofNullable(k).map(s->RelaxationCondition.valueOf(s)).orElseThrow());
    }
    
    public static StdSerializer<ExistOperator> getExistOperatorSerializer() 
    {
        return buildJsonSerializer(ExistOperator.class
                                                , (value, gen, serializers)->{
                                                    if(Objects.nonNull(value))
                                                        gen.writeString(value.getSymbol());
                                                });
    }
    
    public static StdDeserializer<ExistOperator> getExistOperatorDeserializer() 
    {
        return buildJsonDeserializer(ExistOperator.class
                                                    , (p, ctxt)->Optional.ofNullable(p.getValueAsString()).map(s->ExistOperator.getFromSymbol(s)).orElseThrow());
    }
}
