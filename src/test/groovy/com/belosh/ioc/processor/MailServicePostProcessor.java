package com.belosh.ioc.processor;

import com.belosh.ioc.exception.BeanInstantiationException;
import com.belosh.ioc.service.MailService;

public class MailServicePostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String id) throws BeanInstantiationException {
        if ("newMailService".equals(id)) {
            ((MailService) bean).setPostProcessorBeforeCfg("Before initialization configuration executed");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String id) throws BeanInstantiationException {
        if ("newMailService".equals(id)) {
            ((MailService) bean).setPostProcessorAfterCfg("After initialization configuration executed");
        }
        return bean;
    }
}
