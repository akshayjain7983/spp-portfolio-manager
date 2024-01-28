package spp.portfolio.constituents.rules;

import java.util.Optional;

public interface Security
{
    SecurityType getType();
    
    Long getSecurityId();
    
    <T> Optional<T> getAttributeValue(String attributeKey, Class<T> attributeType);
    
    <T> void setAttributeValue(String attributeKey, Optional<T> attributeValue);
}
