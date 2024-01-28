package spp.portfolio.constituents.rules.inmemory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;

import spp.portfolio.configuration.expose.PortfolioConfigurationController;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rules.inmemory.dao.SecurityDataDao;
import spp.portfolio.constituents.spring.PortfolioConstituentsSpringConfiguration;
import spp.portfolio.manager.utilities.spring.SpringContextHolder;
import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;
import spp.portfolio.model.spring.PortfolioConfigurationConverter;
import spp.portfolio.model.spring.PortfolioModelSpringConfiguration;

@DataJpaTest
@Commit
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EntityScan(basePackages = {"spp.portfolio"})
@EnableJpaRepositories(basePackages = {"spp.portfolio"})
@EnableJpaAuditing
@ContextConfiguration(classes = {PortfolioRebalanceInMemoryExecutor.class, PortfolioRebalanceContextSetupRule.class, PortfolioRebalanceConstituentBuilderRule.class
        , PortfolioRebalancePersistRule.class, PortfolioRebalanceContextCleanupRule.class, SecurityDataDao.class, PortfolioConfigurationController.class
        , PortfolioConfigurationConverter.class, PortfolioModelSpringConfiguration.class, PortfolioConstituentsSpringConfiguration.class
        , SpringContextHolder.class, JacksonAutoConfiguration.class})
class PortfolioRebalanceInMemoryExecutorTest
{
    @Autowired
    private PortfolioRebalanceInMemoryExecutor executor;

    @Test
    void testExecute()
    {
        PortfolioRebalanceCommand command = 
                PortfolioRebalanceCommand.builder()
                .portfolioDefinitionId(30L)
                .date(LocalDate.of(2023, 1, 31))
                .portfolioRebalanceType(PortfolioRebalanceType.INDICATIVE)
                .build();
        
        PortfolioRebalance rebalance = executor.execute(command);
        assertNotNull(rebalance);
        assertNotNull(rebalance.getId());
    }

}
