package com.belosh.ioc.Injector;

import com.belosh.ioc.entity.Bean;
import com.belosh.ioc.parser.BeanDefinition;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ValueInjector extends Injector {

    @Override
    protected Map<String, String> getDependencies(BeanDefinition beanDefinition) {
        return beanDefinition.getDependencies();
    }

    @Override
    protected void injectPropertyIntoSetter(Object beanValue, Method method, Object propertyToInject) throws InvocationTargetException, IllegalAccessException {
        String propertyValue = String.valueOf(propertyToInject);
        Class<?>[] fieldTypes = method.getParameterTypes();
        Class<?> fieldType = fieldTypes[0];
        if (boolean.class.equals(fieldType)) {
            method.invoke(beanValue, Boolean.parseBoolean(propertyValue));
        } else if (int.class.equals(fieldType)) {
            method.invoke(beanValue, Integer.parseInt(propertyValue));
        } else if (double.class.equals(fieldType)) {
            method.invoke(beanValue, Double.parseDouble(propertyValue));
        } else if (long.class.equals(fieldType)) {
            method.invoke(beanValue, Long.parseLong(propertyValue));
        } else if (short.class.equals(fieldType)) {
            method.invoke(beanValue, Short.parseShort(propertyValue));
        } else if (byte.class.equals(fieldType)) {
            method.invoke(beanValue, Byte.parseByte(propertyValue));
        } else if (float.class.equals(fieldType)) {
            method.invoke(beanValue, Float.parseFloat(propertyValue));
        } else if (char.class.equals(fieldType)) {
            method.invoke(beanValue, propertyValue.charAt(0));
        } else {
            method.invoke(beanValue, propertyValue);
        }
    }

    @Override
    protected Object getValueToInject(Map<String, String> dependencies, String propertyName, Map<BeanDefinition, Bean> beanDefinitionToBeanMap) {
        return dependencies.get(propertyName);
    }
}
