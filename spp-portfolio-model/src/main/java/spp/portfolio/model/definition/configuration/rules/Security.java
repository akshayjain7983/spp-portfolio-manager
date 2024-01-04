package spp.portfolio.model.definition.configuration.rules;

public interface Security
{
    SecurityType getType();
    <T> T getAttributeValue(String attributeKey, Class<T> attributeType);  
}
