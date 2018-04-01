package com.belosh.ioc.injector;

import com.belosh.ioc.entity.Bean;
import com.belosh.ioc.exceptions.BeanInstantiationException;
import com.belosh.ioc.reader.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class Injector {
    protected Map<BeanDefinition, Bean> beanDefinitionToBeanMap;

    public Injector(Map<BeanDefinition, Bean> beanDefinitionToBeanMap) {
        this.beanDefinitionToBeanMap = beanDefinitionToBeanMap;
    }

    public void injectDependencies() {
        for (BeanDefinition beanDefinition : beanDefinitionToBeanMap.keySet()) {
            Bean bean = beanDefinitionToBeanMap.get(beanDefinition);
            Map <String, ?> dependencies = getDependencies(beanDefinition);
            for (String propertyName : dependencies.keySet()) {
                try {
                    Method method = getSetterForInjection(bean, propertyName);
                    Object propertyToInject = dependencies.get(propertyName);
                    injectPropertyIntoSetter(bean.getValue(), method, propertyToInject);
                } catch (IllegalAccessException e) {
                    throw new BeanInstantiationException("Setter should have public access type for field: " + propertyName, e);
                } catch (InvocationTargetException e) {
                    throw new BeanInstantiationException("Cannot perform beans instantiation", e);
                }
            }
        }
    }

    private Method getSetterForInjection(Bean bean, String propertyName) {
        Class<?> beanValueClazz = bean.getValue().getClass();
        String setterName = getSetterName(propertyName);
        Method[] methods = beanValueClazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(setterName)) {
                return method;
            }
        }
        throw new BeanInstantiationException("Setter was not found for field: " + propertyName);
    }

    protected abstract  Map<String, ?> getDependencies(BeanDefinition beanDefinition);

    protected abstract void injectPropertyIntoSetter(Object beanValue, Method method, Object propertyToInject) throws InvocationTargetException, IllegalAccessException;

    private String getSetterName( String fieldName) {
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }
}