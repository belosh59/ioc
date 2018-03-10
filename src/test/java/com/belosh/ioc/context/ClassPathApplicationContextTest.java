package com.belosh.ioc.context;

import com.belosh.ioc.exceptions.BeanInstantiationException;
import com.belosh.ioc.service.AllDataTypes;
import com.belosh.ioc.service.MailService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ClassPathApplicationContextTest {
    private ApplicationContext context;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void prepareTest() {
        context = new ClassPathApplicationContext("valid-properties.xml");
    }

    //Single path
    @Test
    public void testCreateBeansFromBeanDefinition() {
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext("valid-properties.xml");
    }
    //Multiple paths
    @Test
    public void testCreateBeansFromBeanDefinitionPaths() {
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext("valid-properties.xml", "valid-data-types.xml");
    }

    @Test
    public void testGetBeanByClass() {
        MailService mailService = context.getBean(MailService.class);
        assertEquals(mailService.getPort(), "3000");
        assertEquals(mailService.getProtocol(), "POP3");
    }

    @Test
    public void testGetBeanByName() {
        Object object = context.getBean("newMailService");
        MailService mailService = (MailService) object;
        assertEquals(mailService.getPort(), "3000");
        assertEquals(mailService.getProtocol(), "POP3");
    }

    @Test
    public void testGetBeanNames() {
        List<String> beanNames = context.getBeanNames();
        List<String> assertList = new ArrayList<>();
        assertList.add("newMailService");
        assertList.add("userService");
        assertList.add("paymentService");
        assertEquals(beanNames, assertList);
    }

    @Test
    public void testBeanMissedDefaultConstructor() {
        expectedEx.expect(BeanInstantiationException.class);
        expectedEx.expectMessage("Default constructor not found for com.belosh.ioc.service.BeanWithoutDefaultConstructor");
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext("invalid-default-constructor.xml");
    }

    @Test
    public void testBeanIncorrectClassName() {
        expectedEx.expect(BeanInstantiationException.class);
        expectedEx.expectMessage("Incorrect class declared in beans configuration xml file");
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext("invalid-class-declaration.xml");
    }

    @Test
    public void testAllDataTypes() {
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext("valid-data-types.xml");
        AllDataTypes allDataTypes = classPathApplicationContext.getBean("allDataTypes", AllDataTypes.class);
        assertEquals(allDataTypes.getIntType(), 5);
        //assertEquals(allDataTypes.getDoubleType(), 3.36);
        assertEquals(allDataTypes.getLongType(), 12345678910L);
        //assertEquals(allDataTypes.getFloatType(), 3.36);
        assertEquals(allDataTypes.getShortType(), 32767);
        assertEquals(allDataTypes.getBooleanType(), true);
        assertEquals(allDataTypes.getByteType(), 127);
        assertEquals(allDataTypes.getCharType(), 't');

    }
}
