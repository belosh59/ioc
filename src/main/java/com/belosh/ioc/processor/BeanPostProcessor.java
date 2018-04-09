package com.belosh.ioc.processor;

import com.belosh.ioc.exception.BeanInstantiationException;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String id) throws BeanInstantiationException;

    Object postProcessAfterInitialization(Object bean, String id) throws BeanInstantiationException;
}
