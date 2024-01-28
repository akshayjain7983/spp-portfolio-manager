package spp.portfolio.constituents.rules.inmemory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.Data;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.manager.utilities.json.JsonUtil;

@Data
public class SecurityImpl implements Security
{
    private final Long securityId;
    private final SecurityType type;
    private final Map<Attribute<?>, Optional<Object>> attributes;

    @Override
    public synchronized <T> Optional<T> getAttributeValue(String attributeKey, Class<T> attributeType)
    {
        Objects.requireNonNull(attributeKey, "attributeKey missing");
        Objects.requireNonNull(attributeType, "attributeType missing");
        Optional<Object> val = attributes.get(Attribute.ofName(attributeKey, null));
        return Optional.ofNullable(val).flatMap(v->v.map(l->JsonUtil.viaJson(l, attributeType)));
    }

    @Override
    public synchronized <T> void setAttributeValue(String attributeKey, Optional<T> attributeValue)
    {
        Objects.requireNonNull(attributeKey, "attributeKey missing");
        attributes.put(Attribute.ofName(attributeKey, attributeValue.getClass()), Optional.ofNullable(attributeValue).flatMap(v->v.map(l->(Object)l)));
    }
}
