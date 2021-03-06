package com.belosh.ioc.reader

import com.belosh.ioc.entity.BeanDefinition
import com.belosh.ioc.exception.ParseXMLException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

abstract class AbstractBeanDefinitionParserITest {

    protected abstract BeanDefinitionReader getBeanDefinitionReader(String xmlFilePath)

    @Rule
    public ExpectedException expectedEx = ExpectedException.none()

    @Test
    void testReadBeanDefinitions(){
        int beanId
        BeanDefinitionReader beanDefinitionReader = getBeanDefinitionReader("valid-properties.xml")
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.readBeanDefinitions()

        beanId=0 //first bean
        Assert.assertEquals("newMailService", beanDefinitions.get(beanId).getId())
        Assert.assertEquals("com.belosh.ioc.service.MailService", beanDefinitions.get(beanId).getBeanClassName())
        Assert.assertEquals("POP3", beanDefinitions.get(beanId).getDependencies().get("protocol"))
        Assert.assertEquals("3000", beanDefinitions.get(beanId).getDependencies().get("port"))

        beanId=1 //second bean
        Assert.assertEquals("userService", beanDefinitions.get(beanId).getId())
        Assert.assertEquals("com.belosh.ioc.service.UserService", beanDefinitions.get(beanId).getBeanClassName())
        Assert.assertEquals("newMailService", beanDefinitions.get(beanId).getRefDependencies().get("mailService"))


        beanId=2 //third bean
        Assert.assertEquals("paymentService", beanDefinitions.get(beanId).getId())
        Assert.assertEquals("com.belosh.ioc.service.PaymentService", beanDefinitions.get(beanId).getBeanClassName())
        Assert.assertEquals("newMailService", beanDefinitions.get(beanId).getRefDependencies().get("mailService"))

    }

    @Test
    void testImportBeanDefinitions(){
        int beanId
        BeanDefinitionReader beanDefinitionReader = getBeanDefinitionReader("valid-import.xml")
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.readBeanDefinitions()

        // Imported Beans
        // ===============
        beanId=0 // first bean
        Assert.assertEquals("newMailService", beanDefinitions.get(beanId).getId())
        Assert.assertEquals("com.belosh.ioc.service.MailService", beanDefinitions.get(beanId).getBeanClassName())
        Assert.assertEquals("POP3", beanDefinitions.get(beanId).getDependencies().get("protocol"))
        Assert.assertEquals("3000", beanDefinitions.get(beanId).getDependencies().get("port"))

        beanId=1 //second bean
        Assert.assertEquals("userService", beanDefinitions.get(beanId).getId())
        Assert.assertEquals("com.belosh.ioc.service.UserService", beanDefinitions.get(beanId).getBeanClassName())
        Assert.assertEquals("newMailService", beanDefinitions.get(beanId).getRefDependencies().get("mailService"))

        beanId=2 //third bean
        Assert.assertEquals("paymentService", beanDefinitions.get(beanId).getId())
        Assert.assertEquals("com.belosh.ioc.service.PaymentService", beanDefinitions.get(beanId).getBeanClassName())
        Assert.assertEquals("5000", beanDefinitions.get(beanId).getDependencies().get("maxAmount"))
        Assert.assertEquals("newMailService", beanDefinitions.get(beanId).getRefDependencies().get("mailService"))
        // ===============

        beanId=3 // Current XML bean
        Assert.assertEquals("anotherPaymentService", beanDefinitions.get(beanId).getId())
        Assert.assertEquals("com.belosh.ioc.service.PaymentService", beanDefinitions.get(beanId).getBeanClassName())
        Assert.assertEquals("10", beanDefinitions.get(beanId).getDependencies().get("maxAmount"))
        Assert.assertEquals("newMailService", beanDefinitions.get(beanId).getRefDependencies().get("mailService"))
    }

    @Test
    void testXMLNotFound(){
        expectedEx.expect(ParseXMLException.class)
        expectedEx.expectMessage("File wrongFileName.xml not found in classpath")
        BeanDefinitionReader beanDefinitionReader = getBeanDefinitionReader("wrongFileName.xml")
        beanDefinitionReader.readBeanDefinitions()
    }
}
