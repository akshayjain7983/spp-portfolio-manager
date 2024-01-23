package spp.portfolio.constituents.rules.inmemory.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.Collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import io.github.funofprograming.context.ApplicationContextKey;
import io.github.funofprograming.context.ConcurrentApplicationContext;
import io.github.funofprograming.context.impl.ConcurrentApplicationContextImpl;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.constituents.util.SqlFiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EntityScan(basePackages = {"spp.portfolio"})
@EnableJpaRepositories
@ContextConfiguration(classes = {SecurityDataDao.class})
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
    void testLoadSecuritiesOne()
    {
        ConcurrentApplicationContext context = new ConcurrentApplicationContextImpl("SecurityDataDaoTest");
        context.add(ApplicationContextKey.of("rebalanceDate", LocalDate.class), LocalDate.now());
        context.add(ApplicationContextKey.of("securityType", SecurityType.class), SecurityType.EQUITY);
        context.add(ApplicationContextKey.of("exchangeCode", String.class), "500086");
        Collection<Security> securities = securityDataDao.loadSecurities(context);
        assertFalse(securities.isEmpty());
        assertEquals(securities.size(), 1);
    }

    @Test
    void testLoadSecuritiesAll()
    {
        ConcurrentApplicationContext context = new ConcurrentApplicationContextImpl("SecurityDataDaoTest");
        context.add(ApplicationContextKey.of("rebalanceDate", LocalDate.class), LocalDate.now());
        context.add(ApplicationContextKey.of("securityType", SecurityType.class), SecurityType.EQUITY);
        Collection<Security> securities = securityDataDao.loadSecurities(context);
        assertFalse(securities.isEmpty());
        assertNotEquals(securities.size(), 0);
        assertNotEquals(securities.size(), 1);
    }
}
