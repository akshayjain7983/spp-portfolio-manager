package spp.portfolio.model.spring;

import static spp.portfolio.model.json.JsonUtil.fromJson;
import static spp.portfolio.model.json.JsonUtil.toJson;

import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import spp.portfolio.model.definition.configuration.PortfolioConfiguration;

@Component
public class PortfolioConfigurationConverter implements GenericConverter
{
    private static final Set<ConvertiblePair> convertibleTypes = 
            Set.of(new ConvertiblePair(PortfolioConfiguration.class, String.class), new ConvertiblePair(String.class, PortfolioConfiguration.class));
    
    @Override
    public Set<ConvertiblePair> getConvertibleTypes()
    {
        return convertibleTypes;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
    {
        if(isPortfolioConfiguration(sourceType) && isString(targetType))
            return toJson((PortfolioConfiguration)source);
        
        else if(isString(sourceType) && isPortfolioConfiguration(targetType))
            return fromJson((String)source, PortfolioConfiguration.class);
        
        throw new IllegalArgumentException("Invalid types for this converter");
    }
    
    private boolean isPortfolioConfiguration(TypeDescriptor typeDescriptor)
    {
        return PortfolioConfiguration.class.isAssignableFrom(typeDescriptor.getType());
    }
    
    private boolean isString(TypeDescriptor typeDescriptor)
    {
        return String.class.isAssignableFrom(typeDescriptor.getType());
    }
    
    @PostConstruct
    public void setup()
    {
        ((ConfigurableConversionService)DefaultConversionService.getSharedInstance()).addConverter(this);
    }
}
