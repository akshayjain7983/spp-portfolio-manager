package spp.portfolio.manager.utilities.jpa;

import static spp.portfolio.manager.utilities.json.JsonUtil.fromJson;
import static spp.portfolio.manager.utilities.json.JsonUtil.toJson;

import jakarta.persistence.AttributeConverter;

public interface JsonJpaConverter<T> extends AttributeConverter<T, java.lang.String>
{
    Class<T> getDataClass();

    @Override
    default String convertToDatabaseColumn(T attribute)
    {
        return toJson(attribute);
    }

    @Override
    default T convertToEntityAttribute(String dbData)
    {
        return fromJson(dbData, getDataClass());
    }
}
