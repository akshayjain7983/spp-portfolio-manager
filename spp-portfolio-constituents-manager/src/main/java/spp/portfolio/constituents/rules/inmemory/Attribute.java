package spp.portfolio.constituents.rules.inmemory;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"name", "literalValue"})
public class Attribute<T>
{
    private String name;
    private Class<T> type;
    private String literalValue;
    
    public static <T> Attribute<T> ofName(String name, Class<T> type)
    {
        return new Attribute<>(name, type, null);
    }
    
    public static <T> Attribute<T> ofLiteral(String literalValue, Class<T> type)
    {
        return new Attribute<>(null, type, literalValue);
    }
    
    @Override
    public String toString()
    {
        return StringUtils.isNotEmpty(literalValue) ? literalValue : name;
    }
}
