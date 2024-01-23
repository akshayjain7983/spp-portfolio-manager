package spp.portfolio.constituents.rules.inmemory;

import java.util.Map;
import java.util.Objects;

import lombok.Data;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.manager.utilities.json.JsonUtil;

@Data
public class SecurityImpl implements Security
{
    private final Long securityId;
    private final SecurityType type;
    private final Map<Attribute<?>, Object> attributes;

    @Override
    public synchronized <T> T getAttributeValue(String attributeKey, Class<T> attributeType)
    {
        Object val = attributes.get(Attribute.ofName(attributeKey, null));
        if(Objects.nonNull(val))
        {
            return JsonUtil.viaJson(val, attributeType);
        }
        
        return null;
    }

    @Override
    public synchronized <T> void setAttributeValue(String attributeKey, T attributeValue)
    {
        if(Objects.nonNull(attributeValue))
            attributes.put(Attribute.ofName(attributeKey, attributeValue.getClass()), attributeValue);
    }
}
