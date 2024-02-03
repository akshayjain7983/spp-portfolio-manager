package spp.portfolio.constituents.rules.inmemory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import spp.portfolio.constituents.rebalance.DefaultPortfolioRebalanceExecutor;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceExecutor;
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
@ContextConfiguration(classes = {DefaultPortfolioRebalanceExecutor.class, SecurityDataDao.class, PortfolioConfigurationController.class
        , PortfolioConfigurationConverter.class, PortfolioModelSpringConfiguration.class, PortfolioConstituentsSpringConfiguration.class
        , SpringContextHolder.class, JacksonAutoConfiguration.class})
class DefaultPortfolioRebalanceExecutorTest
{
    @Autowired
    private PortfolioRebalanceExecutor executor;

    @Test
    void testExecute() throws InterruptedException, ExecutionException
    {
        PortfolioRebalanceCommand command = 
                PortfolioRebalanceCommand.builder()
                .runId(UUID.randomUUID())
                .portfolioDefinitionId(30L)
                .date(LocalDate.of(2023, 2, 1))
                .portfolioRebalanceType(PortfolioRebalanceType.INDICATIVE)
                .build();
        
        CompletableFuture<PortfolioRebalance> rebalanceFuture = executor.execute(command);
        while(!rebalanceFuture.isDone())
            Thread.sleep(1000);
        PortfolioRebalance rebalance = rebalanceFuture.get();
        assertNotNull(rebalance);
        assertNotNull(rebalance.getId());
    }

}
