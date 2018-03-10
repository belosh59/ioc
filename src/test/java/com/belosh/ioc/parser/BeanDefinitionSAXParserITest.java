package com.belosh.ioc.parser;

public class BeanDefinitionSAXParserITest extends AbstractBeanDefinitionParserITest {
    @Override
    protected BeanDefinitionReader getBeanDefinitionReader(String xmlFilePath) {
        return new BeanDefinitionSAXParser(xmlFilePath);
    }
}
