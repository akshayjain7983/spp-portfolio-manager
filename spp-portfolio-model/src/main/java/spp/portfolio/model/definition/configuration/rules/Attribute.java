package spp.portfolio.model.definition.configuration.rules;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class Attribute<T>
{
    private String name;
    private Class<T> type;
    private String literalValue;
    
    @Override
    public String toString()
    {
        return StringUtils.isNotEmpty(literalValue) ? literalValue : name;
    }
}
