package spp.portfolio.manager.utilities.jpa;

import jakarta.persistence.AttributeConverter;

public interface EnumJpaConverter<E extends Enum<E>> extends AttributeConverter<E, String>
{
    Class<E> getEnumClass();
    
    @Override
    default String convertToDatabaseColumn(E attribute)
    {
        return attribute.name();
    }

    @Override
    default E convertToEntityAttribute(String dbData)
    {
        return Enum.valueOf(getEnumClass(), dbData);
    }
}
