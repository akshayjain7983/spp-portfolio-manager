package spp.portfolio.model.marketdata.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.NavigableSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import spp.portfolio.model.marketdata.Holiday;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EntityScan(basePackages = {"spp.portfolio"})
@EnableJpaRepositories
@ContextConfiguration(classes = {HolidayRepository.class})
class HolidayRepositoryTest
{
    @Autowired
    private HolidayRepository holidayRepository;
    
    @Test
    public void testSearchByExchangeAndSegmentAndDate()
    {
        Optional<Holiday> holidayTrue = holidayRepository.searchByExchangeAndSegmentAndDate("BSE", "Equity", LocalDate.of(2023, 12, 25));
        assertNotNull(holidayTrue);
        assertTrue(holidayTrue.isPresent());
        
        Optional<Holiday> holidayFalse = holidayRepository.searchByExchangeAndSegmentAndDate("BSE", "Equity", LocalDate.of(2023, 12, 19));
        assertNotNull(holidayFalse);
        assertFalse(holidayFalse.isPresent());
    }
    
    @Test
    public void testIsHoliday()
    {
        assertTrue(holidayRepository.isHoliday("BSE", "Equity", LocalDate.of(2023, 12, 25)));
        assertFalse(holidayRepository.isHoliday("BSE", "Equity", LocalDate.of(2023, 12, 19)));
    }

    @Test
    public void testSearchByExchangeAndSegmentAndDateBetween()
    {
        NavigableSet<Holiday> holidays = holidayRepository.searchByExchangeAndSegmentAndDateBetween("BSE", "Equity", LocalDate.now().minusYears(1), LocalDate.now());
        assertNotNull(holidays);
        assertFalse(holidays.isEmpty());
    }
    
    @Test
    public void testFindNextBusinessDate()
    {
        LocalDate currentDate = LocalDate.now().minusDays(123);
        LocalDate nextBusinessDate = holidayRepository.findNextBusinessDate("BSE", "Equity", currentDate);
        assertNotNull(nextBusinessDate);
        assertTrue(nextBusinessDate.isAfter(currentDate));
    }
    
    @Test
    public void testFindPreviousBusinessDate()
    {
        LocalDate currentDate = LocalDate.now().minusDays(123);
        LocalDate previousBusinessDate = holidayRepository.findPreviousBusinessDate("BSE", "Equity", currentDate);
        assertNotNull(previousBusinessDate);
        assertTrue(previousBusinessDate.isBefore(currentDate));
    }
}
