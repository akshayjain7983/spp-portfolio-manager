package spp.portfolio.model.spring.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "spp.portfolio-manager")
public class SppPortfolioManagerConfiguration
{
    private SppPortfolioConstituentsManagerConfiguration constituentManagerConfiguration = new SppPortfolioConstituentsManagerConfiguration();
}
