package spp.portfolio.constituents.rules.simple;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.github.funofprograming.context.ConcurrentApplicationContext;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name = "MarketValueSecurityWeightCapper", value = MarketValueSecurityWeightCapper.class),
    @Type(name = "PortfolioAmountLimitSecurityWeightCapper", value = PortfolioAmountLimitSecurityWeightCapper.class)
})
public interface SecurityWeightCapper
{
    Collection<Security> capWeights(Collection<Security> securities, ConcurrentApplicationContext context);
}
