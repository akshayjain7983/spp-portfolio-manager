package spp.portfolio.constituents.spring;

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.databind.Module;

import spp.portfolio.constituents.json.PortfolioConfigurationModule;
import spp.portfolio.constituents.rebalance.CompositePortfolioRebalanceStage;
import spp.portfolio.constituents.rules.inmemory.PortfolioRebalanceConstituentBuilderStage;
import spp.portfolio.constituents.rules.inmemory.PortfolioRebalanceContextCleanupStage;
import spp.portfolio.constituents.rules.inmemory.PortfolioRebalanceContextSetupStage;
import spp.portfolio.constituents.rules.inmemory.PortfolioRebalancePersistStage;
import spp.portfolio.constituents.util.SqlFiles;

@Configuration
@EnableAsync
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
    Executor portfolioRebalanceTaskExecutor() 
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("PortfolioRebalanceTaskExecutor-");
        executor.initialize();
        return executor;
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
