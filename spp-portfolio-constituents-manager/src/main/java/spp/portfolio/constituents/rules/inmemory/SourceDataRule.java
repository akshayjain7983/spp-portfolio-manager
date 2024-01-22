package spp.portfolio.constituents.rules.inmemory;

import java.util.ArrayList;
import java.util.Collection;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;

@Data
public class SourceDataRule implements PortfolioRule
{
    private String exchange;
    private SecurityType securityType;

    @Override
    public boolean doExecute(ConcurrentApplicationContext context)
    {
        return true;
    }
    
    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> securitiesFromSource = new ArrayList<>();
        //TODO: fetch from repo for date in context following data: securities, prices, pScore, analytics
        return securitiesFromSource;
    }
}
