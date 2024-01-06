package spp.portfolio.configuration.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"spp.portfolio.configuration.dao"})
public class PortfolioConfigurationManagerSpringConfiguration
{
}
