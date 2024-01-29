package spp.portfolio.constituents.rules.inmemory;

import java.util.ArrayList;
import java.util.Collection;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class ParentPortfolioRule implements PortfolioRule
{
    private Long parentPortfolioId;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> securitiesFromParent = new ArrayList<>();
        //TODO: fetch from repo for date in context and parentPortfolio with parentPortfolioId, the following data: securities, prices, pScore, analytics
        return securitiesFromParent;
    }
}
