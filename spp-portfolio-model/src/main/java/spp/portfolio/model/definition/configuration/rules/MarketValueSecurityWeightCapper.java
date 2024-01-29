package spp.portfolio.model.definition.configuration.rules;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class MarketValueSecurityWeightCapper implements SecurityWeightCapper
{
    private Map<String, BigDecimal> capWeightsByGroup;
    private WeightCappingStrategy weightCappingStrategy;
}
