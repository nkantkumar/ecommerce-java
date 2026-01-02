package com.fix;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MyBean implements BeanNameAware,
        BeanFactoryAware,
        ApplicationContextAware {

    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;

    @Override
    public void setBeanName(String name) {
        // Called with bean's name in container
        System.out.println("Bean name: " + name);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        // Get reference to BeanFactory
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        // Get reference to ApplicationContext
        this.applicationContext = ctx;
    }

}
