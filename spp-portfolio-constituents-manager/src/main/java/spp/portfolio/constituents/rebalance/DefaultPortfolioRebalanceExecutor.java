package spp.portfolio.constituents.rebalance;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import spp.portfolio.model.rebalance.PortfolioRebalance;

@Component
public class DefaultPortfolioRebalanceExecutor implements PortfolioRebalanceExecutor
{
    @Autowired
    @Qualifier("standardPortfolioRebalance")
    private PortfolioRebalanceStage standardPortfolioRebalance;
    
    @Override
    @Async("portfolioRebalanceTaskExecutor")
    public CompletableFuture<PortfolioRebalance> execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        
        PortfolioRebalance portfolioRebalance = null;
        
        //In future we might want to implement different combination of stages and make that as a configurable methodology in portfolio definition
        portfolioRebalance = standardPortfolioRebalance.execute(portfolioRebalanceCommand);
        
        return CompletableFuture.completedFuture(portfolioRebalance);
    }
}
