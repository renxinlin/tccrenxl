package com.tcc.renxl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class TccSpringContextUtil implements ApplicationContextAware
{

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext _applicationContext)
            throws BeansException
    {
        applicationContext = _applicationContext;
    }

    public static Object getBean(String beanName)
    {
        return applicationContext.getBean(beanName);
    }

    public static Object getBean(Class clazz)
    {
        return applicationContext.getBean(clazz);
    }

}
