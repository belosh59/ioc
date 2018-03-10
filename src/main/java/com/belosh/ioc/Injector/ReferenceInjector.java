package com.belosh.ioc.Injector;

import com.belosh.ioc.entity.Bean;
import com.belosh.ioc.parser.BeanDefinition;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ReferenceInjector extends Injector {

    @Override
    protected Map<String, String> getDependencies(BeanDefinition beanDefinition) {
        return beanDefinition.getRefDependencies();
    }

    @Override
    protected void injectPropertyIntoSetter(Object beanValue, Method method, Object propertyToInject) throws InvocationTargetException, IllegalAccessException {
        method.invoke(beanValue, propertyToInject);
    }

    @Override
    protected Object getValueToInject(Map<String, String> dependencies, String propertyName, Map<BeanDefinition, Bean> beanDefinitionToBeanMap) {
        for (BeanDefinition beanDefinition : beanDefinitionToBeanMap.keySet()) {
            if (beanDefinition.getId().equals(propertyName)) {
                return beanDefinitionToBeanMap.get(beanDefinition).getValue();
            }
        }
        return null;
    }
}
