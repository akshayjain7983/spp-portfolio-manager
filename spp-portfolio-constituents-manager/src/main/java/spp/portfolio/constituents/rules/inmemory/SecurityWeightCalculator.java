package spp.portfolio.constituents.rules.inmemory;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import spp.portfolio.constituents.rules.Security;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name = "SecurityMarketValueWeightCalculator", value = SecurityMarketValueWeightCalculator.class)
})
public interface SecurityWeightCalculator
{
    Collection<Security> setupWeights(Collection<Security> securities);
}
