package spp.portfolio.model.definition.configuration.rules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name = "SourceDataRule", value = SourceDataRule.class),
    @Type(name = "ParentPortfolioRule", value = ParentPortfolioRule.class),
    @Type(name = "FiltersRule", value = FiltersRule.class),
    @Type(name = "LoopPortfolioRule", value = LoopPortfolioRule.class),
    @Type(name = "WeightCalculationRule", value = WeightCalculationRule.class),
    @Type(name = "WeightCappingRule", value = WeightCappingRule.class)
})
public interface PortfolioRule
{
}
