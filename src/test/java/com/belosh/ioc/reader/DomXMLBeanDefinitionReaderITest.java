package com.belosh.ioc.reader;


public class DomXMLBeanDefinitionReaderITest extends AbstractBeanDefinitionParserITest{

    @Override
    protected BeanDefinitionReader getBeanDefinitionReader(String xmlFilePath) {
        return new DomXMLBeanDefinitionReader(xmlFilePath);
    }
}
