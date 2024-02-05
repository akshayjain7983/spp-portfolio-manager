package spp.portfolio.constituents.rules.inmemory;

import java.util.Collection;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class RankingRule implements PortfolioRule
{
    private Ranker ranker;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> rankedSecurities = ranker.rank(securities, context);
        return rankedSecurities;
    }
}
