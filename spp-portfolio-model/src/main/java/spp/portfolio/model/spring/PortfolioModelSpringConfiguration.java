package spp.portfolio.model.spring;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;

import spp.portfolio.model.json.PortfolioConfigurationModule;

@Configuration
@EntityScan(basePackages = {"spp.portfolio.model"})
public class PortfolioModelSpringConfiguration
{
    @Bean
    Module portfolioConfigurationModule()
    {
        return new PortfolioConfigurationModule();
    }
}
