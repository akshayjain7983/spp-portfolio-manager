package spp.portfolio.constituents.rules.inmemory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rules.Security;

public class SecurityDerivedDataRule implements PortfolioRule
{
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
                .map(s->derivedDataCalc.apply(s, sec->sec.setAttributeValue("market_cap", marketCapCalc.apply(Optional.ofNullable(sec))))) //market_cap
                .collect(Collectors.toList());
    }
    
    private final BiFunction<Security, Consumer<Security>, Security> derivedDataCalc = 
            (security, securityCalc)->{
                
                securityCalc.accept(security);
                return security;
            };
    
    private final Function<Optional<Security>, Optional<BigDecimal>> marketCapCalc = 
            security->security.flatMap(s->{
              
                Optional<BigDecimal> totalOutstandingShares = s.getAttributeValue("total_outstanding_shares", BigDecimal.class);
                Optional<BigDecimal> closePrice = s.getAttributeValue("close_price", BigDecimal.class);
                Optional<BigDecimal> marketCap = totalOutstandingShares.flatMap(tos->closePrice.map(p->p.multiply(tos)));
                return marketCap;
            });

}
