package com.belosh.ioc.parser;


public class BeanDefinitionDOMParserITest extends AbstractBeanDefinitionParserITest{

    @Override
    protected BeanDefinitionReader getBeanDefinitionReader(String xmlFilePath) {
        return new BeanDefinitionDOMParser(xmlFilePath);
    }
}
