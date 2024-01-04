package spp.portfolio.configuration.spring;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"spp.portfolio.configuration.dao"})
@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan(basePackages = {"spp.portfolio.configuration.model"})
public class SpringConfiguration
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
