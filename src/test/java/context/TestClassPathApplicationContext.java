package context;

import entiry.Bean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.MailService;
import service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestClassPathApplicationContext {
    MailService mailService;
    UserService userService;
    Map<String, String> dependencies;
    Map<String, String> refDependencies;
    ApplicationContext context;

    @Before
    public void prepareTest() {
        mailService = new MailService();
        userService = new UserService();

        dependencies = new HashMap<>();
        dependencies.put("protocol", "POP3");
        dependencies.put("port", "3000");

        refDependencies = new HashMap<>();
        refDependencies.put("mailService", "mailService");

        context = new ClassPathApplicationContext("context.xml");
    }

    @Test
    public void testCreateBeansFromBeanDefinition() {
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext("context.xml");
        classPathApplicationContext.createBeansFromBeanDefinition();

    }

    @Test
    public void testInjectDependencies(){
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext("context.xml");

        //Before injection of dependencies
        assertNull(mailService.getPort());
        assertNull(mailService.getProtocol());

        // Injection of dependencies
        classPathApplicationContext.injectDependencies(mailService, dependencies);

        //After injection of dependencies
        assertEquals(mailService.getPort(), "3000");
        assertEquals(mailService.getProtocol(), "POP3");
    }

    @Test
    public void testInjectRefDependencies(){
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext("context.xml");
        assertNull(userService.getMailService());
        classPathApplicationContext.injectRefDependencies(userService, refDependencies);
        assertEquals(userService.getMailService().getProtocol(), "POP3");
        assertEquals(userService.getMailService().getPort(), "3000");

    }

    @Test
    public void testGetBeanByClass() {
        MailService mailService = (MailService) context.getBean(MailService.class);
        assertEquals(mailService.getPort(), "3000");
        assertEquals(mailService.getProtocol(), "POP3");
    }

    @Test
    public void testGetBeanByName() {
        Object object = context.getBean("mailService");
        MailService mailService = (MailService) object;
        assertEquals(mailService.getPort(), "3000");
        assertEquals(mailService.getProtocol(), "POP3");
    }

    @Test
    public void testGetBeanNames() {
        List<String> beanNames = context.getBeanNames();
        List<String> assertList = new ArrayList<>();
        assertList.add("mailService");
        assertList.add("userService");
        assertList.add("paymentService");
        assertEquals(beanNames, assertList);
    }

}
