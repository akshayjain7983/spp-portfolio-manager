package spp.portfolio.constituents.rules;

public interface Security
{
    SecurityType getType();
    
    Long getSecurityId();
    
    <T> T getAttributeValue(String attributeKey, Class<T> attributeType);
    
    <T> void setAttributeValue(String attributeKey, T attributeValue);
}
