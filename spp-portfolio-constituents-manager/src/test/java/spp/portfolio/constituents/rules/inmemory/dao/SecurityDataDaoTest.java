package spp.portfolio.constituents.rules.inmemory.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import io.github.funofprograming.context.ApplicationContext;
import io.github.funofprograming.context.Key;
import io.github.funofprograming.context.KeyType;
import io.github.funofprograming.context.impl.ApplicationContextImpl;
import spp.portfolio.constituents.rules.simple.Security;
import spp.portfolio.constituents.rules.simple.SecurityType;
import spp.portfolio.constituents.rules.simple.dao.SecurityDataDao;
import spp.portfolio.constituents.util.SqlFiles;
import spp.portfolio.model.spring.configuration.SppPortfolioManagerConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EntityScan(basePackages = {"spp.portfolio"})
@EnableJpaRepositories
@ContextConfiguration(classes = {SecurityDataDao.class})
@EnableConfigurationProperties(SppPortfolioManagerConfiguration.class)
class SecurityDataDaoTest
{
    @Autowired
    private SecurityDataDao securityDataDao;

    @BeforeAll
    static void setUp() throws Exception
    {
        SqlFiles.load();
    }

    @Test
    void testLoadSecuritiesAll()
    {
        ApplicationContext context = new ApplicationContextImpl("SecurityDataDaoTest");
        Map<String, Collection<SecurityType>> exchangesWithSecurityTypes = Map.of("BSE", Set.of(SecurityType.EQUITY));
        context.add(Key.of("rebalanceDate", LocalDate.class), LocalDate.of(2018, 11, 1));
        context.add(Key.of("exchangesWithSecurityTypes", KeyType.<Map<String, Collection<SecurityType>>>of(Map.class)), exchangesWithSecurityTypes);
        Collection<Security> securities = securityDataDao.loadSecurities(context);
        assertFalse(securities.isEmpty());
        assertNotEquals(securities.size(), 0);
        assertNotEquals(securities.size(), 1);
    }
}
