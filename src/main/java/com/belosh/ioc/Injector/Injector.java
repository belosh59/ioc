package com.belosh.ioc.Injector;

import com.belosh.ioc.entity.Bean;
import com.belosh.ioc.exceptions.BeanInstantiationException;
import com.belosh.ioc.parser.BeanDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class Injector {
    public  void injectDependencies(Map<BeanDefinition, Bean> beanDefinitionToBeanMap) {
        for (BeanDefinition beanDefinition : beanDefinitionToBeanMap.keySet()) {
            Bean bean = beanDefinitionToBeanMap.get(beanDefinition);
            Map <String, String> dependencies = getDependencies(beanDefinition);
            for (String propertyName : dependencies.keySet()) {
                try {
                    Class<?> beanValueClazz = bean.getValue().getClass();
                    Field field = beanValueClazz.getDeclaredField(propertyName);
                    String setterName = getSetterName(field.getName());
                    Method method = beanValueClazz.getDeclaredMethod(setterName, field.getType());
                    Object propertyToInject = getValueToInject(dependencies, propertyName, beanDefinitionToBeanMap);
                    injectPropertyIntoSetter(bean.getValue(), method, propertyToInject);
                } catch (NoSuchMethodException e) {
                    throw new BeanInstantiationException("Setter was not found for field: " + propertyName, e);
                } catch (NoSuchFieldException e) {
                    throw new BeanInstantiationException("Field was not found: " + propertyName, e);
                } catch (IllegalAccessException e) {
                    throw new BeanInstantiationException("Setter should have public access type for field: " + propertyName, e);
                } catch (InvocationTargetException e) {
                    throw new BeanInstantiationException("Cannot perform beans instantiation", e);
                }
            }
        }
    }

    protected abstract  Map<String, String> getDependencies(BeanDefinition beanDefinition);

    protected abstract void injectPropertyIntoSetter(Object beanValue, Method method, Object propertyToInject) throws InvocationTargetException, IllegalAccessException;

    protected abstract Object getValueToInject(Map <String, String> dependencies, String propertyName, Map<BeanDefinition, Bean> beanDefinitionToBeanMap);

    private String getSetterName(final String line) {
        return "set" + Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}