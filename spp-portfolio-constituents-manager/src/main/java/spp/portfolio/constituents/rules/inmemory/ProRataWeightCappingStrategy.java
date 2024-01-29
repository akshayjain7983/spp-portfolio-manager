package spp.portfolio.constituents.rules.inmemory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ProRataWeightCappingStrategy implements WeightCappingStrategy
{
    @Override
    public Map<Long, BigDecimal> capWeights(Map<Long, BigDecimal> existingDistribution, BigDecimal targetTotalDistribution)
    {
        BigDecimal existingTotalDistribution = existingDistribution.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return
                existingDistribution.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, e->e.getValue().divide(existingTotalDistribution).multiply(targetTotalDistribution)));
    }
}
