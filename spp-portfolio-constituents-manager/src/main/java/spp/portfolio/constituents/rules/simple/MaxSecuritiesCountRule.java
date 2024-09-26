package spp.portfolio.constituents.rules.simple;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;

@Data
public class MaxSecuritiesCountRule implements PortfolioRule
{
    private Long maxSecuritesCount;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        return Optional.ofNullable(securities)
                                .orElse(Collections.emptyList())
                                .stream()
                                .sorted(Comparator.comparing(s->s.getAttributeValue("rebalance_rank", Long.class).orElse(Long.MAX_VALUE)))
                                .limit(maxSecuritesCount)
                                .collect(Collectors.toList());
    }
}
