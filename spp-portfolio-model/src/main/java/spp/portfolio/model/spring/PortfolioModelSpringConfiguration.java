package spp.portfolio.model.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;

import spp.portfolio.model.json.PortfolioConfigurationModule;

@Configuration
public class PortfolioModelSpringConfiguration
{
    @Bean
    Module modelPortfolioConfigurationModule()
    {
        return new PortfolioConfigurationModule();
    }
}
