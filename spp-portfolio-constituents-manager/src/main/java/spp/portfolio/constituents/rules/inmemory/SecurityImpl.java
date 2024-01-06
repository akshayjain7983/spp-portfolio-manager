package spp.portfolio.constituents.rules.inmemory;

import java.util.Map;

import lombok.Data;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;

@Data
public class SecurityImpl implements Security
{
    private final String securityId;
    private final SecurityType type;
    private final Map<String, Object> attributes;

    @Override
    public <T> T getAttributeValue(String attributeKey, Class<T> attributeType)
    {
        return attributeType.cast(attributes.get(attributeKey));
    }

}
