package spp.portfolio.constituents.stage.inmemory;

import static io.github.funofprograming.context.impl.ApplicationContextHolder.getGlobalContext;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioConfigurationKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceTransactionsKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioSizeCurrentKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.rebalanceContextNameBuilder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceStage;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.inmemory.PortfolioConfiguration;
import spp.portfolio.model.rebalance.PortfolioConstituent;
import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceTransaction;

public class PortfolioRebalanceConstituentBuilderStage implements PortfolioRebalanceStage
{
    @Override
    public PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        ConcurrentApplicationContext context = (ConcurrentApplicationContext) getGlobalContext(rebalanceContextNameBuilder.apply(portfolioRebalanceCommand));
        PortfolioConfiguration configuration = context.fetch(portfolioConfigurationKey);
        Collection<Security> constituents = configuration.getPortfolioConfigurationConstituents().execute(context);
        PortfolioRebalance portfolioRebalance = context.fetch(portfolioRebalanceKey);
        setupPortfolioRebalance(portfolioRebalance, configuration, constituents, context);
        return portfolioRebalance;
    }

    private void setupPortfolioRebalance(PortfolioRebalance portfolioRebalance, PortfolioConfiguration configuration, Collection<Security> constituents, ConcurrentApplicationContext context)
    {
        Collection<PortfolioConstituent> portfolioConstituents = 
                constituents.stream()
                .map(c->{
                    return
                            PortfolioConstituent.builder()
                            .portfolioRebalance(portfolioRebalance)
                            .securityId(c.getSecurityId())
                            .price(c.getAttributeValue("rebalance_price", BigDecimal.class).orElse(null))
                            .units(c.getAttributeValue("rebalance_units", Long.class).orElse(null))
                            .investmentMarketValue(c.getAttributeValue("market_value", BigDecimal.class).orElse(null))
                            .weight(c.getAttributeValue("rebalance_weight", BigDecimal.class).orElse(null))
                            .build();
                })
                .collect(Collectors.toList());
        
        portfolioRebalance.setPortfolioConstituents(portfolioConstituents);
        
        Collection<PortfolioRebalanceTransaction> portfolioRebalanceTransactions = context.fetch(portfolioRebalanceTransactionsKey);
        portfolioRebalance.setPortfolioRebalanceTransactions(portfolioRebalanceTransactions);
        BigDecimal portfolioSizeCurrent = context.fetch(portfolioSizeCurrentKey);
        BigDecimal rebalanceInvestmentMarketValue = 
                portfolioRebalance.getPortfolioConstituents()
                .stream()
                .map(c->Optional.ofNullable(c.getInvestmentMarketValue()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal rebalanceCash = portfolioSizeCurrent.subtract(rebalanceInvestmentMarketValue);
        
        portfolioRebalance.setInvestmentMarketValue(rebalanceInvestmentMarketValue);
        portfolioRebalance.setPortfolioCash(rebalanceCash);
    }
}
