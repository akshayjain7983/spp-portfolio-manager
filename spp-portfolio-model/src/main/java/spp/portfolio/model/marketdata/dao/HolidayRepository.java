package spp.portfolio.model.marketdata.dao;

import java.time.LocalDate;
import java.util.NavigableSet;
import java.util.Optional;

import spp.portfolio.manager.utilities.jpa.ReadOnlyJpaRepository;
import spp.portfolio.model.marketdata.Holiday;

public interface HolidayRepository extends ReadOnlyJpaRepository<Holiday, Long>
{
    Optional<Holiday> searchByExchangeAndSegmentAndDate(String exchange, String segment, LocalDate date);

    NavigableSet<Holiday> searchByExchangeAndSegmentAndDateBetween(String exchange, String segment, LocalDate fromDate, LocalDate toDate);
    
    default boolean isHoliday(String exchange, String segment, LocalDate date)
    {
        return searchByExchangeAndSegmentAndDate(exchange, segment, date).isPresent();
    }
    
    default LocalDate findNextBusinessDate(String exchange, String segment, LocalDate currentDate)
    {
        LocalDate nextBusinessDate = currentDate.plusDays(1);
        while(isHoliday(exchange, segment, nextBusinessDate))
        {
            nextBusinessDate = nextBusinessDate.plusDays(1);
        }
        
        return nextBusinessDate;
    }
    
    default LocalDate findPreviousBusinessDate(String exchange, String segment, LocalDate currentDate)
    {
        LocalDate previousBusinessDate = currentDate.minusDays(1);
        while(isHoliday(exchange, segment, previousBusinessDate))
        {
            previousBusinessDate = previousBusinessDate.minusDays(1);
        }
        
        return previousBusinessDate;
    }
}
