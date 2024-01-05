package spp.portfolio.model.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware
{
    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        SpringContextHolder.applicationContext = applicationContext;        
    }
    
    public static <T> T getBean(Class<T> requiredType)
    {
        return applicationContext.getBean(requiredType);
    }
    
    public static <T> T getBean(String name, Class<T> requiredType)
    {
        return applicationContext.getBean(name, requiredType);
    }
    
    public static <T> T getBean(Class<T> requiredType, Object...args)
    {
        return applicationContext.getBean(requiredType, args);
    }

}
