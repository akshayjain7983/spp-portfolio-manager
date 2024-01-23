package spp.portfolio.constituents.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;

import spp.portfolio.constituents.json.PortfolioConfigurationModule;
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
}
