package spp.portfolio.configuration.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
@EnableJpaRepositories(basePackages = {"spp.portfolio.configuration.dao"})
public class PortfolioConfigurationManagerSpringConfiguration
{
    @Bean
    RepositoryRestConfigurer repositoryRestConfigurer() 
    {
        return RepositoryRestConfigurer.withConfig(repositoryRestConfig->repositoryRestConfig
                                                                                                    .setRepositoryDetectionStrategy(RepositoryDetectionStrategies.ANNOTATED)
                                                                                                    .getExposureConfiguration()
                                                                                                    .disablePutForCreation()
                                                                                                    .disablePatchOnItemResources()
                                                                                                    .disablePutOnItemResources()
                                                                                                    );
    }
}
