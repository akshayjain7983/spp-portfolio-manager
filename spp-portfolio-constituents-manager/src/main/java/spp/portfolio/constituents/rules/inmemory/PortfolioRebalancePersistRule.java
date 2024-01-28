package spp.portfolio.constituents.rules.inmemory;

import static io.github.funofprograming.context.impl.ApplicationContextHolder.getGlobalContext;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.rebalanceContextNameBuilder;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import jakarta.transaction.Transactional;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceRule;
import spp.portfolio.constituents.rules.inmemory.dao.PortfolioRebalanceRepository;
import spp.portfolio.model.rebalance.PortfolioRebalance;

@Component
public class PortfolioRebalancePersistRule implements PortfolioRebalanceRule
{
    @Autowired
    private PortfolioRebalanceRepository portfolioRebalanceRepository;

    @Override
    public int getOrder()
    {
        return 2;
    }

    @Override
    @Transactional
    public PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        ConcurrentApplicationContext context = (ConcurrentApplicationContext) getGlobalContext(rebalanceContextNameBuilder.apply(portfolioRebalanceCommand));
        PortfolioRebalance portfolioRebalance = context.fetch(portfolioRebalanceKey);
        Optional<PortfolioRebalance> portfolioRebalanceActivePrevious = portfolioRebalanceRepository.findByPortfolioDefinitionAndDateAndRebalanceTypeAndIsActive(portfolioRebalance.getPortfolioDefinition(), portfolioRebalance.getDate(), portfolioRebalance.getRebalanceType(), Boolean.TRUE);
        portfolioRebalanceActivePrevious.ifPresent(pr->{
            pr.setIsActive(Boolean.FALSE);
            portfolioRebalanceRepository.save(pr);
        });
        return portfolioRebalanceRepository.save(portfolioRebalance);
    }

}
