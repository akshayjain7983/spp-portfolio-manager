package spp.portfolio.constituents.rules.inmemory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class ComparisonExpression implements Expression<Boolean>
{
    private String leftSide;
    private ComparisonOperator operator;
    private String rightSide;
    
    @Override
    public Class<Boolean> resultType()
    {
        return Boolean.class;
    }
    
    @Override
    public Boolean execute(Optional<Security> security, ConcurrentApplicationContext context)
    {
        if(security.isEmpty())
            return Boolean.FALSE;
        
        Object leftSideObject = security.get().getAttributeValue(leftSide);
        if(Objects.isNull(leftSideObject))
            return Boolean.FALSE;
        
        
        Object rightSideObject = parseRightSideObject(leftSideObject);
        if(Objects.isNull(rightSideObject))
            return Boolean.FALSE;
        
        return compare(leftSideObject, rightSideObject);
    }

    private Boolean compare(Object leftSideObject, Object rightSideObject)
    {
        switch (leftSideObject)
        {
            case String s->
            {
                return compareStrings((String)leftSideObject, (String)rightSideObject);
            }
            case LocalDate t->
            {
                ZonedDateTime leftSide = ZonedDateTime.of((LocalDate)leftSideObject, LocalTime.MIDNIGHT, ZoneOffset.UTC);
                ZonedDateTime rightSide = ZonedDateTime.of((LocalDate)leftSideObject, LocalTime.MIDNIGHT, ZoneOffset.UTC);
                return compareZonedDateTimes(leftSide, rightSide);
            }
            case LocalDateTime t->
            {
                ZonedDateTime leftSide = ZonedDateTime.of((LocalDateTime)leftSideObject, ZoneOffset.UTC);
                ZonedDateTime rightSide = ZonedDateTime.of((LocalDateTime)leftSideObject, ZoneOffset.UTC);
                return compareZonedDateTimes(leftSide, rightSide);
            }
            case ZonedDateTime t->
            {
                return compareZonedDateTimes((ZonedDateTime)leftSideObject, (ZonedDateTime)rightSideObject);
            }
            case Number n->
            {
                return compareNumbers((Number)leftSideObject, (Number)rightSideObject);
            }
            
            default -> throw new IllegalArgumentException("Unexpected type: " + leftSideObject.getClass());
        }
    }

    private Boolean compareNumbers(Number leftSideObject, Number rightSideObject)
    {
        BigDecimal leftSide = new BigDecimal(leftSideObject.toString());
        BigDecimal rightSide = new BigDecimal(rightSideObject.toString());
        
        switch (operator)
        {
            case GREATER_THAN: return leftSide.compareTo(rightSide) > 0;
            case GREATER_THAN_EQUAL: return leftSide.compareTo(rightSide) >= 0;
            case LESS_THAN: return leftSide.compareTo(rightSide) < 0;
            case LESS_THAN_EQUAL: return leftSide.compareTo(rightSide) <= 0;
            case NOT_EQUAL: return leftSide.compareTo(rightSide) != 0;
            case EQUAL: return leftSide.compareTo(rightSide) == 0;
            default:
                throw new IllegalArgumentException("Unexpected value: " + operator);
        }
    }
    
    private Boolean compareZonedDateTimes(ZonedDateTime leftSideObject, ZonedDateTime rightSideObject)
    {
        switch (operator)
        {
            case GREATER_THAN: return leftSideObject.isAfter(rightSideObject);
            case GREATER_THAN_EQUAL: return !leftSideObject.isBefore(rightSideObject);
            case LESS_THAN: return leftSideObject.isBefore(rightSideObject);
            case LESS_THAN_EQUAL: return !leftSideObject.isAfter(rightSideObject);
            case NOT_EQUAL: return !leftSideObject.isEqual(rightSideObject);
            case EQUAL: return leftSideObject.isEqual(rightSideObject);
            default:
                throw new IllegalArgumentException("Unexpected value: " + operator);
        }
    }

    private Boolean compareStrings(String leftSideObject, String rightSideObject)
    {
        switch (operator)
        {
            case GREATER_THAN: return leftSideObject.compareTo(rightSideObject) > 0;
            case GREATER_THAN_EQUAL: return leftSideObject.compareTo(rightSideObject) >= 0;
            case LESS_THAN: return leftSideObject.compareTo(rightSideObject) < 0;
            case LESS_THAN_EQUAL: return leftSideObject.compareTo(rightSideObject) <= 0;
            case NOT_EQUAL: return !leftSideObject.equalsIgnoreCase(rightSideObject);
            case EQUAL: return leftSideObject.equalsIgnoreCase(rightSideObject);
            default:
                throw new IllegalArgumentException("Unexpected value: " + operator);
        }
    }

    private Object parseRightSideObject(Object leftSideObject)
    {
        switch (leftSideObject)
        {
            case String s->
            {
                return rightSide;
            }
            case LocalDate t->
            {
                return DateTimeFormatter.ISO_LOCAL_DATE.parse(rightSide);
            }
            case LocalDateTime t->
            {
                return DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(rightSide);
            }
            case ZonedDateTime t->
            {
                return DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(rightSide);
            }
            case Integer n->
            {
                return Integer.valueOf(rightSide);
            }
            case Long n->
            {
                return Long.valueOf(rightSide);
            }
            case Float n->
            {
                return Float.valueOf(rightSide);
            }
            case Double n->
            {
                return Double.valueOf(rightSide);
            }
            case BigInteger n->
            {
                return new BigInteger(rightSide);
            }
            case BigDecimal n->
            {
                return new BigDecimal(rightSide);
            }
            
            default -> throw new IllegalArgumentException("Unexpected type: " + leftSideObject.getClass());
        }
    }

    @Override
    public String toString()
    {
        return "ComparisonExpression [" + leftSide + " " + operator.getSymbol() + " " + rightSide + "]";
    }
}
