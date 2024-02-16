package spp.portfolio.constituents.rebalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import spp.portfolio.model.exception.SppException;
import spp.portfolio.model.rebalance.PortfolioRebalance;

@Log4j2
@Component
public class DefaultPortfolioRebalanceExecutor implements PortfolioRebalanceExecutor
{
    @Autowired
    @Qualifier("standardPortfolioRebalance")
    private PortfolioRebalanceStage standardPortfolioRebalance;

    @Override
    public PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
	try
	{
	    PortfolioRebalance portfolioRebalance = null;

	    // In future we might want to implement different combination of stages and make that as a configurable methodology in portfolio definition
	    portfolioRebalance = standardPortfolioRebalance.execute(portfolioRebalanceCommand);

	    return portfolioRebalance;

	} 
	catch (Exception e)
	{
	    log.error(e);
	    throw new SppException(e);
	}
    }
}
