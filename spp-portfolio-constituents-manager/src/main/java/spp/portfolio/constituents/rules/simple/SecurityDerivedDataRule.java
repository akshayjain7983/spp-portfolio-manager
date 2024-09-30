package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.safeDivide;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.sppPortfolioManagerConfigurationKey;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.model.spring.configuration.SppPortfolioManagerConfiguration;

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
	SppPortfolioManagerConfiguration sppPortfolioManagerConfiguration = context.fetch(sppPortfolioManagerConfigurationKey);
	
        return
                Optional.ofNullable(securities)
                .orElse(Collections.emptyList())
                .parallelStream()
                .map(marketCapCalc) //market_cap
                .map(s->pScoreAvgCalc.apply(s, sppPortfolioManagerConfiguration))
                .collect(Collectors.toList());
    }
    
    private final BiFunction<Security, SppPortfolioManagerConfiguration, Security> pScoreAvgCalc =
	    (security, config)->{
		Integer forecastpscoreHistoryDays = config.getConstituentManagerConfiguration().getForecastpscoreHistoryDays();
		Collection<Integer> forecastpscoreHistoryDaysAverages = config.getConstituentManagerConfiguration().getForecastpscoreHistoryDaysAverages();
		Integer forecastpscoreHistoryDaysAveragesMaxDays = forecastpscoreHistoryDaysAverages.stream().max(Comparator.comparingInt(Integer::intValue)).get();
		forecastpscoreHistoryDaysAveragesMaxDays = forecastpscoreHistoryDaysAveragesMaxDays < forecastpscoreHistoryDays ? forecastpscoreHistoryDaysAveragesMaxDays : forecastpscoreHistoryDays;
		Collection<String> forecastPeriods = config.getConstituentManagerConfiguration().getForecastPeriods();
		if(CollectionUtils.isNotEmpty(forecastPeriods))
		{
		    for(String fp:forecastPeriods)
		    {
			BigDecimal sum = BigDecimal.ZERO;
			
			for(int i=1; i<=forecastpscoreHistoryDaysAveragesMaxDays; i++)
			{
			    String attrKey = fp + "_forecast_p_score_T-"+i;
			    Optional<BigDecimal> pScore = security.getAttributeValue(attrKey, BigDecimal.class);
			    if(pScore.isEmpty())
				break; //no point going forward since one val in sum is missing
			    
			    sum = sum.add(pScore.get());
			    if(forecastpscoreHistoryDaysAverages.contains(i))
			    {
				BigDecimal avg = safeDivide.apply(sum, BigDecimal.valueOf(i));
				String avgKey = fp+"_forecast_p_score_last_"+i+"d_average";
				security.setAttributeValue(avgKey, Optional.ofNullable(avg));
			    }
			}
		    }
		}
		    
		return security;
	    };
    
    private final Function<Security, Security> marketCapCalc = 
	    security->{
		
		Optional<BigDecimal> totalOutstandingShares = security.getAttributeValue("total_outstanding_shares", BigDecimal.class);
                Optional<BigDecimal> closePrice = security.getAttributeValue("close_price", BigDecimal.class);
                Optional<BigDecimal> marketCap = totalOutstandingShares.flatMap(tos->closePrice.map(p->p.multiply(tos)));
                security.setAttributeValue("market_cap", marketCap);
		return security;
	    };
            
            

}
