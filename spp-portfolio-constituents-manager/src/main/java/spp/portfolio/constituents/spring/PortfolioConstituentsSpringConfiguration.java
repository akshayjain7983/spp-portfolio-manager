package spp.portfolio.constituents.spring;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;

import spp.portfolio.constituents.json.PortfolioConfigurationModule;

@Configuration
@EntityScan(basePackages = {"spp.portfolio.constituents"})
public class PortfolioConstituentsSpringConfiguration
{
    @Bean
    Module portfolioConfigurationModule()
    {
        return new PortfolioConfigurationModule();
    }
}
