package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;

@Data
public class PortfolioAmountLimitSecurityWeightCapper implements SecurityWeightCapper, SecuritiesOutpointTraceable
{
    @Override
    public Collection<Security> capWeights(Collection<Security> securities, ConcurrentApplicationContext context)
    {
	BigDecimal portfolioSizeCurrent = context.fetch(portfolioSizeCurrentKey);
        BigDecimal marketValueTotal = findSumOfSecurityAttribute.apply(securities, "market_value");
        BigDecimal diff = marketValueTotal.subtract(portfolioSizeCurrent);
        
        if(BigDecimal.ZERO.compareTo(diff) < 0)
        {
            context.fetch(isWeightCappingRun).set(true);
            BigDecimal cappingToBeApplied = BigDecimal.ONE.subtract(safeDivide.apply(diff, marketValueTotal));
            Optional.ofNullable(securities).orElse(Collections.emptyList())
            .stream()
            .forEach(s->{
        	
        	Optional<Long> minRunLockedRebalanceUnits = s.getAttributeValue("min_run_locked_rebalance_units", Long.class).or(()->Optional.ofNullable(0L));
        	Optional<Long> rebalanceUnitsCurrent = s.getAttributeValue("rebalance_units", Long.class);
        	Optional<Long> rebalanceUnitsRequired = rebalanceUnitsCurrent.map(ru->cappingToBeApplied.multiply(BigDecimal.valueOf(ru)).longValue());
                Optional<Long> rebalanceUnits = rebalanceUnitsRequired.flatMap(ru->minRunLockedRebalanceUnits.filter(mrlru->mrlru>ru).or(()->Optional.ofNullable(ru))); //current rebalance units cannot be less than min run locked units
                s.setAttributeValue("rebalance_units", rebalanceUnits);
            });
        }
        
        securities = Optional.ofNullable(securities).orElse(Collections.emptyList()).stream()
                		.filter(s->s.getAttributeValue("rebalance_units", Long.class).filter(ru->ru>0L).isPresent())
                		.filter(s->s.getAttributeValue("market_value", BigDecimal.class).isPresent())
                		.collect(Collectors.toList()); //filter out 0 rebalance_units/weight securities
	return securities;
    }

    @Override
    public String getOutpointRepresentation()
    {
	return "Zero Weight @ " + this.toString();
    }

}
