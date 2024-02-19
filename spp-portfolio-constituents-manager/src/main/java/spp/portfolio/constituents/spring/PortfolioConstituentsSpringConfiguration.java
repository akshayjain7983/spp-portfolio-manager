package spp.portfolio.constituents.spring;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.databind.Module;

import spp.portfolio.constituents.json.PortfolioConfigurationModule;
import spp.portfolio.constituents.rebalance.CompositePortfolioRebalanceStage;
import spp.portfolio.constituents.stage.PortfolioRebalanceContextCleanupStage;
import spp.portfolio.constituents.stage.PortfolioRebalancePersistStage;
import spp.portfolio.constituents.stage.inmemory.PortfolioRebalanceConstituentBuilderStage;
import spp.portfolio.constituents.stage.inmemory.PortfolioRebalanceContextSetupStage;
import spp.portfolio.constituents.util.SqlFiles;

@Configuration
public class PortfolioConstituentsSpringConfiguration
{
    
    public PortfolioConstituentsSpringConfiguration()
    {
        SqlFiles.load();
    }
    
    @Bean
    Module constituentsPortfolioConfigurationModule()
    {
        return new PortfolioConfigurationModule();
    }
    
    @Bean
    @Qualifier("portfolioRebalanceTaskExecutor")
    ExecutorService portfolioRebalanceTaskExecutor() 
    {
	return
            	Executors.newFixedThreadPool(2
            		, r->{
            		    AtomicInteger counter = new AtomicInteger(1);
            		    return new Thread(Thread.currentThread().getThreadGroup(), r, "PortfolioRebalanceTaskExecutor-%d".formatted(counter.incrementAndGet()));
            		});
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    PortfolioRebalanceContextSetupStage portfolioRebalanceContextSetupStage()
    {
        return new PortfolioRebalanceContextSetupStage();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    PortfolioRebalanceConstituentBuilderStage portfolioRebalanceConstituentBuilderStage()
    {
        return new PortfolioRebalanceConstituentBuilderStage();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    PortfolioRebalancePersistStage portfolioRebalancePersistStage()
    {
        return new PortfolioRebalancePersistStage();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    PortfolioRebalanceContextCleanupStage portfolioRebalanceContextCleanupStage()
    {
        return new PortfolioRebalanceContextCleanupStage();
    }
    
    @Bean
    @Qualifier("standardPortfolioRebalance")
    CompositePortfolioRebalanceStage standardPortfolioRebalance()
    {
        return new CompositePortfolioRebalanceStage(List.of(
                    portfolioRebalanceContextSetupStage()
                    , portfolioRebalanceConstituentBuilderStage()
                    , portfolioRebalancePersistStage()
                    , portfolioRebalanceContextCleanupStage()
                ));
    }
}
