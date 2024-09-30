package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceCommandKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceLastKey;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.model.rebalance.PortfolioRebalance;

/*
 * This must run immediately after SourceDataRule and before FiltersRule
 */

@Data
public class MinRunRule implements PortfolioRule
{    
    private Long minRunDays;
    private BigDecimal minRunLockMarketValueRatio;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
	PortfolioRebalance portfolioRebalanceLast = context.fetch(portfolioRebalanceLastKey);
	PortfolioRebalanceCommand portfolioRebalanceCommand = context.fetch(portfolioRebalanceCommandKey);
	LocalDate portfolioRebalanceDate = portfolioRebalanceCommand.getDate();
	
	Optional.ofNullable(portfolioRebalanceLast)
	.map(PortfolioRebalance::getPortfolioConstituents)
	.orElse(Collections.emptyList())
	.forEach(constituent->
	{
	    Long securityId = constituent.getSecurityId();
	    LocalDate inPortfolioSince = Optional.ofNullable(constituent.getInPortfolioSince()).orElse(portfolioRebalanceLast.getDate());
	    Long days = ChronoUnit.DAYS.between(inPortfolioSince, portfolioRebalanceDate);
	    if(days<minRunDays)
	    {
		Optional<Security> securityCurrent = securities.stream().filter(s->s.getSecurityId().equals(securityId)).findFirst();
		securityCurrent.ifPresent(s->{
		    
		    s.setAttributeValue("min_run_locked", Optional.ofNullable(Boolean.TRUE));
		    s.setAttributeValue("min_run_locked_since", Optional.ofNullable(inPortfolioSince));
		    
		    Optional<Long> constituentUnitsLocked = Optional.ofNullable(constituent.getUnits()).map(u->minRunLockMarketValueRatio.multiply(BigDecimal.valueOf(u)).longValue());
		    s.setAttributeValue("min_run_locked_rebalance_units", constituentUnitsLocked);
		});
	    }
	});
	
	return securities;
    }
}
