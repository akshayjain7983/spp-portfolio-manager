package spp.portfolio.constituents.rules.inmemory;

import static spp.portfolio.manager.utilities.json.JsonUtil.fromJson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class ComparisonExpression implements Expression<Boolean>
{
    private Attribute<?> leftSide;
    private ComparisonOperator operator;
    private Attribute<?> rightSide;
    
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
        
        Object leftSideObject = parseAttribute(leftSide, security);
        if(Objects.isNull(leftSideObject))
            return Boolean.FALSE;
        
        
        Object rightSideObject = parseAttribute(rightSide, security);
        if(Objects.isNull(rightSideObject))
            return Boolean.FALSE;
        
        return compare(leftSideObject, rightSideObject);
    }
    
    private Object parseAttribute(Attribute<?> attribute, Optional<Security> security)
    {
        Class<?> attributeType = attribute.getType();
        String literalValue = attribute.getLiteralValue();
        if(Objects.nonNull(literalValue))
        {
            return fromJson(literalValue, attributeType);
        }
        else 
        {
            return security.flatMap(s->s.getAttributeValue(attribute.getName(), attributeType)).orElse(null);
        }
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
    
    @Override
    public String toString()
    {
        return "ComparisonExpression [" + leftSide + " " + operator.getSymbol() + " " + rightSide + "]";
    }
}
