package spp.portfolio.constituents.rules.inmemory;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class FiltersRule implements PortfolioRule
{
    private CompoundFilter rootFilter;

    @Override
    public boolean doExecute(ConcurrentApplicationContext context)
    {
        return true;
    }
    
    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        return
                Optional.ofNullable(securities)
                .orElse(Collections.emptyList())
                .parallelStream()
                .map(Optional::ofNullable)
                .map(s->rootFilter.execute(s, context))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
