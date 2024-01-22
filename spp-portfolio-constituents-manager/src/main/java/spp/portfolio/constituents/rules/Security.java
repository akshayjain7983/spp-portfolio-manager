package spp.portfolio.constituents.rules;

public interface Security
{
    SecurityType getType();
    
    Long getSecurityId();
    
    <T> T getAttributeValue(String attributeKey, Class<T> attributeType);
    
    default Object getAttributeValue(String attributeKey)
    {
        return getAttributeValue(attributeKey, Object.class);
    }
}
