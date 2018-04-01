package com.belosh.ioc.reader;

public class SAXBeanDefinitionReaderITest extends AbstractBeanDefinitionParserITest {
    @Override
    protected BeanDefinitionReader getBeanDefinitionReader(String xmlFilePath) {
        return new SAXBeanDefinitionReader(xmlFilePath);
    }
}
