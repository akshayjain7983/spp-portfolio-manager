package spp.portfolio.constituents.rules.inmemory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.model.definition.configuration.rules.SortingRankerAttribute;

@Data
public class SortingRanker implements Ranker
{
    private Collection<SortingRankerAttribute> sortingRankerAttributes;

    @Override
    public Collection<Security> rank(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Comparator<Security> sortingComparator = buildSortingRankerComparator();
        if(Objects.isNull(sortingComparator))
            return securities;
        
        AtomicLong rebalanceRanks = new AtomicLong(0);
        Collection<Security> securitiesRanked = 
                securities
                .stream()
                .sorted(sortingComparator)
                .map(s->{
                    s.setAttributeValue("rebalance_rank", Optional.of(rebalanceRanks.incrementAndGet()));
                    return s;
                })
                .collect(Collectors.toList());
        return securitiesRanked;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Comparator<Security> buildSortingRankerComparator()
    {
        Comparator<Security> securityComparator = null;
        if(CollectionUtils.isEmpty(sortingRankerAttributes))
            return null;
        
        Iterator<SortingRankerAttribute> sortingRankerAttributesIterator = sortingRankerAttributes.iterator();
        SortingRankerAttribute primary = sortingRankerAttributesIterator.next();
        securityComparator = Comparator.<Security, Comparable>comparing(securityComparatorKeyExtractor(primary), ascendingOrDescending(primary));
        while(sortingRankerAttributesIterator.hasNext())
        {
            SortingRankerAttribute secondary = sortingRankerAttributesIterator.next();
            securityComparator = securityComparator.thenComparing(securityComparatorKeyExtractor(secondary), ascendingOrDescending(secondary));
        }
        
        return securityComparator;
    }
    
    @SuppressWarnings("rawtypes")
    private Function<Security, Comparable> securityComparatorKeyExtractor(SortingRankerAttribute sortingRankerAttribute)
    {
        return security ->
                                    security
                                    .getAttributeValue(sortingRankerAttribute.attribute().getName(), sortingRankerAttribute.attribute().getType())
                                    .map(v->{
                                        Comparable comparableValue = null;
                                        switch (v)
                                        {
                                            case Number n -> comparableValue = new BigDecimal(n.toString());
                                            case String s -> comparableValue = (String)s;
                                            case LocalDate t -> comparableValue = ZonedDateTime.of((LocalDate)v, LocalTime.MIDNIGHT, ZoneOffset.UTC);
                                            case LocalDateTime t -> comparableValue = ZonedDateTime.of((LocalDateTime)v, ZoneOffset.UTC);
                                            case ZonedDateTime t -> comparableValue = (ZonedDateTime)v;
                                            case Instant t -> comparableValue = ZonedDateTime.ofInstant((Instant)v, ZoneOffset.UTC);
                                            default -> comparableValue = null;
                                        }
                                        return comparableValue;
                                    })
                                    .orElseGet(()->{
                                        
                                        Class<?> attributeType = sortingRankerAttribute.attribute().getType();
                                        boolean descending = sortingRankerAttribute.descending();
                                        Comparable comparableValue = null;
                                        if(Number.class.isAssignableFrom(attributeType))
                                            comparableValue = new BigDecimal((descending ? Long.MIN_VALUE : Long.MAX_VALUE));
                                        else if(String.class.isAssignableFrom(attributeType))
                                            comparableValue = descending ? String.valueOf((char)0).intern() : String.valueOf((char)127).intern();
                                        else if(LocalDate.class.isAssignableFrom(attributeType)
                                                    || LocalDateTime.class.isAssignableFrom(attributeType)
                                                    || ZonedDateTime.class.isAssignableFrom(attributeType)
                                                    || Instant.class.isAssignableFrom(attributeType))
                                            comparableValue = ZonedDateTime.of(LocalDate.of((descending ? -9999 : 9999), 1, 1), LocalTime.MIDNIGHT, ZoneOffset.UTC);
                                        
                                        return comparableValue;
                                    });
    }
    
    @SuppressWarnings("rawtypes")
    private Comparator ascendingOrDescending(SortingRankerAttribute sortingRankerAttribute)
    {
        return sortingRankerAttribute.descending() ? Comparator.reverseOrder() : Comparator.naturalOrder();
    }
}
