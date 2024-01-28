package spp.portfolio.constituents.rebalance;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import spp.portfolio.model.rebalance.PortfolioRebalance;

@RequiredArgsConstructor
public class CompositePortfolioRebalanceStage implements PortfolioRebalanceStage
{
    private final List<PortfolioRebalanceStage> portfolioRebalanceStages;

    @Override
    public PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        List<PortfolioRebalanceStage> portfolioRebalanceStagesExecuted = new ArrayList<>(1);
        
        try 
        {
            PortfolioRebalance portfolioRebalance = null;
            
            for(PortfolioRebalanceStage stage: portfolioRebalanceStages)
            {
                portfolioRebalanceStagesExecuted.add(stage);
                portfolioRebalance = stage.execute(portfolioRebalanceCommand);
            }
            
            return portfolioRebalance;
        }
        catch(Throwable throwable)
        {
            for(PortfolioRebalanceStage stage: portfolioRebalanceStages)
            {
                if(!portfolioRebalanceStagesExecuted.contains(stage) && stage.isExecuteAlways())
                    stage.execute(portfolioRebalanceCommand);
            }
            
            throw throwable;
        }
    }

}
