package spp.portfolio.constituents.rules.inmemory;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class MaxSecuritiesRule implements PortfolioRule
{
    private Long maxSecurites;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        return Optional.ofNullable(securities)
                                .orElse(Collections.emptyList())
                                .stream()
                                .sorted(Comparator.comparing(s->s.getAttributeValue("rebalance_rank", Long.class).orElse(Long.MAX_VALUE)))
                                .limit(maxSecurites)
                                .collect(Collectors.toList());
    }
}
