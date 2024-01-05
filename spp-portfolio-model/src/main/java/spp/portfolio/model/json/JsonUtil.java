package spp.portfolio.model.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import spp.portfolio.model.spring.SpringContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class JsonUtil
{
    public static ObjectMapper getObjectMapper()
    {
        return SpringContextHolder.getBean(ObjectMapper.class);
    }
    
    public static String toJson(Object object)
    {
        return toJson(object, getObjectMapper());
    }
    
    public static String toJson(Object object, ObjectMapper objectMapper)
    {
        try
        {
            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            log.error("Unable to convert to Json", e);
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T fromJson(String json, Class<T> requiredType)
    {
        return fromJson(json, requiredType, getObjectMapper());
    }
    
    public static <T> T fromJson(String json, Class<T> requiredType, ObjectMapper objectMapper)
    {
        try
        {
            return objectMapper.readValue(json, requiredType);
        }
        catch (JsonProcessingException e)
        {
            log.error("Unable to parse from Json", e);
            throw new RuntimeException(e);
        }
    }
}
