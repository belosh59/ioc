package com.belosh.ioc.context

import com.belosh.ioc.exceptions.BeanInstantiationException
import com.belosh.ioc.service.AllDataTypes
import com.belosh.ioc.service.MailService
import com.belosh.ioc.service.PaymentService
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import static org.junit.Assert.assertNotNull

class ClassPathApplicationContextTest extends GroovyTestCase {


    @Rule
    ExpectedException expectedEx = ExpectedException.none()

//    @Before
//    void prepareTest() {
//        println "before"
//        context = new ClassPathApplicationContext("valid-properties.xml")
//    }

    //Single path
    @Test
    void testCreateBeansFromBeanDefinition() {
        new ClassPathApplicationContext("valid-properties.xml")
    }
    //Multiple paths
    @Test
    void testCreateBeansFromBeanDefinitionPaths() {
        new ClassPathApplicationContext("valid-properties.xml", "valid-data-types.xml")
    }

    @Test
    void testGetBeanByClass() {
        ApplicationContext context = new ClassPathApplicationContext("valid-properties.xml")
        MailService mailService = context.getBean(MailService.class)
        assert mailService.getPort() == "3000"
        assert mailService.getProtocol() =="POP3"
    }

    @Test
    void testGetBeanByName() {
        ApplicationContext context = new ClassPathApplicationContext("valid-properties.xml")
        Object object = context.getBean("newMailService")
        MailService mailService = (MailService) object
        assert mailService.getPort() == "3000"
        assert mailService.getProtocol() =="POP3"
    }

    @Test
    void testGetBeanNames() {
        ApplicationContext context = new ClassPathApplicationContext("valid-properties.xml")
        List<String> beanNames = context.getBeanNames()
        List expectedBeanNames = ["newMailService", "userService", "paymentService"]
        for (String beanName : beanNames) {
            assert expectedBeanNames.remove(beanName)
        }

        assert expectedBeanNames.isEmpty()
    }

//    @Test
//    void testBeanMissedDefaultConstructor() {
//        expectedEx.expect(BeanInstantiationException.class)
//        expectedEx.expectMessage("Default constructor not found for com.belosh.ioc.service.BeanWithoutDefaultConstructor")
//        new ClassPathApplicationContext("invalid-default-constructor.xml")
//    }
//
//    @Test
//    void testBeanIncorrectClassName() {
//        expectedEx.expect(BeanInstantiationException.class)
//        expectedEx.expectMessage("Incorrect class declared in beans configuration xml file")
//        new ClassPathApplicationContext("invalid-class-declaration.xml")
//    }

    @Test
    void testAllDataTypes() {
        ApplicationContext classPathApplicationContext = new ClassPathApplicationContext("valid-data-types.xml")
        AllDataTypes allDataTypes = classPathApplicationContext.getBean("allDataTypes", AllDataTypes.class)
        assert allDataTypes.getIntType() == 5
        //assert allDataTypes.getDoubleType() == 3.36
        assert allDataTypes.getLongType() == 12345678910L
        //assert allDataTypes.getFloatType() == 3.36
        assert allDataTypes.getShortType() == 32767
        assert allDataTypes.getBooleanType()
        assert allDataTypes.getByteType() == 127
        assert allDataTypes.getCharType() == 't'
    }

    @Test
    void validReferenceValue() {
        ApplicationContext context = new ClassPathApplicationContext("valid-properties.xml")
        PaymentService bean = context.getBean(PaymentService.class)
        assertNotNull(bean.getMailService())
    }

    @Test
    void invalidReferenceValue() {
        expectedEx.expect(BeanInstantiationException.class)
        expectedEx.expectMessage("Reference bean not found: newMailServiceInvalid")
        new ClassPathApplicationContext("invalid-refdependency.xml")
    }
}
