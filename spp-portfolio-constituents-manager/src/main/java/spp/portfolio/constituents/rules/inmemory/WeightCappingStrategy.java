package spp.portfolio.constituents.rules.inmemory;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name = "ProRataWeightCappingStrategy", value = ProRataWeightCappingStrategy.class)
})
public interface WeightCappingStrategy
{
    Map<Long, BigDecimal> capWeights(Map<Long, BigDecimal> existingDistribution, BigDecimal targetTotalDistribution);
}
