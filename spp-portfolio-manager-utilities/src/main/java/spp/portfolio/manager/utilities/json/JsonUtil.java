package spp.portfolio.manager.utilities.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import spp.portfolio.manager.utilities.spring.SpringContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class JsonUtil
{
    public static ObjectMapper getObjectMapper()
    {
        try 
        {
            return SpringContextHolder.getBean(ObjectMapper.class);
        } 
        catch(Exception e)
        {
            log.warn("Unable to get ObjectMapper from Spring context. Will return default one.", e);
            return buildObjectMapper();
        }        
    }
    
    public static ObjectMapper buildObjectMapper()
    {
        return
                JsonMapper.builder()
                .findAndAddModules()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
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
    
    public static <T> T viaJson(Object source, Class<T> requiredType)
    {
        return viaJson(source, requiredType, getObjectMapper());
    }
    
    public static <T> T viaJson(Object source, Class<T> requiredType, ObjectMapper objectMapper)
    {
        return objectMapper.convertValue(source, requiredType);
    }
}
