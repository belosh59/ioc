package com.belosh.ioc.processor;

import com.belosh.ioc.entity.BeanDefinition
import com.belosh.ioc.exception.BeanInstantiationException;

class BeanDefinitionCounter implements BeanFactoryPostProcessor {
    int beanDefinitionCount

    @Override
    void postProcessBeanFactory(List<BeanDefinition> definitions) {
        for (BeanDefinition beanDefinition : definitions) {
            beanDefinitionCount += 1
        }
        assertBeanDefinitionCount()
    }

    void assertBeanDefinitionCount() {
        if (beanDefinitionCount != 3) {
            throw new BeanInstantiationException("Not enough beans definitions in default valid-propertie-with-processors.xml")
        }
    }
}
