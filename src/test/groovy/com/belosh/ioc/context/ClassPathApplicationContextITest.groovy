package com.belosh.ioc.context

import com.belosh.ioc.exception.BeanInstantiationException
import com.belosh.ioc.service.AllDataTypes
import com.belosh.ioc.service.MailService
import com.belosh.ioc.service.PaymentService
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.junit.Assert.*

class ClassPathApplicationContextITest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none()

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
        assertEquals("3000", mailService.getPort())
        assertEquals("POP3", mailService.getProtocol())
    }

    @Test
    void testGetBeanByName() {
        ApplicationContext context = new ClassPathApplicationContext("valid-properties.xml")
        MailService mailService = (MailService) context.getBean("newMailService")
        assertEquals("3000", mailService.getPort())
        assertEquals("POP3", mailService.getProtocol())
    }

    @Test
    void testGetBeanNames() {
        ApplicationContext context = new ClassPathApplicationContext("valid-properties.xml")
        List<String> beanNames = context.getBeanNames()
        List expectedBeanNames = ["newMailService", "userService", "paymentService"]
        for (String beanName : beanNames) {
            assert expectedBeanNames.remove(beanName)
        }

        assertTrue(expectedBeanNames.isEmpty())
    }

    @Test
    void testBeanMissedDefaultConstructor() {
        expectedEx.expect(BeanInstantiationException.class)
        expectedEx.expectMessage("Default constructor not found for com.belosh.ioc.service.BeanWithoutDefaultConstructor")
        new ClassPathApplicationContext("invalid-default-constructor.xml")
    }

    @Test
    void testBeanIncorrectClassName() {
        expectedEx.expect(BeanInstantiationException.class)
        expectedEx.expectMessage("Incorrect class declared in beans configuration xml file")
        new ClassPathApplicationContext("invalid-class-declaration.xml")
    }

    @Test
    void testAllDataTypes() {
        ApplicationContext classPathApplicationContext = new ClassPathApplicationContext("valid-data-types.xml")
        AllDataTypes allDataTypes = classPathApplicationContext.getBean("allDataTypes", AllDataTypes.class)
        assertEquals(allDataTypes.getIntType(), 5)
        assertEquals(allDataTypes.getDoubleType(), 3.36d, 0)
        assertEquals(allDataTypes.getLongType(), 12345678910L)
        assertEquals(allDataTypes.getFloatType(), 3.36f, 0)
        assertEquals(allDataTypes.getShortType(), 32767)
        assertTrue(allDataTypes.getBooleanType())
        assertEquals(allDataTypes.getByteType(), 127)
        assertEquals((char)'t', allDataTypes.getCharType())
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

    @Test
    void beanPostProcessorFactory() {
        new ClassPathApplicationContext("valid-propertie-with-processors.xml")
    }

    @Test
    void testInit() {
        ApplicationContext context = new ClassPathApplicationContext("valid-properties.xml")
        MailService mailService = context.getBean("newMailService", MailService.class)
        assertTrue(mailService.isInitExecuted())
    }

    @Test
    void testPostProcessBeforeInitialization() {
        ApplicationContext context = new ClassPathApplicationContext("valid-propertie-with-processors.xml")
        MailService mailService = context.getBean("newMailService", MailService.class)
        assertEquals("Before initialization configuration executed", mailService.postProcessorBeforeCfg)
        assertEquals("After initialization configuration executed", mailService.postProcessorAfterCfg)
    }


}
