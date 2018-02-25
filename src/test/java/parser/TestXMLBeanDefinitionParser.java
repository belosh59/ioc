package parser;

import org.junit.Test;

import java.util.List;

public class TestXMLBeanDefinitionParser {

    @Test
    public void testReadBeanDefinitions(){
        XMLBeanDefinitionParser xmlBeanDefinitionParser = new XMLBeanDefinitionParser();
        xmlBeanDefinitionParser.setPath("context.xml");
        List<BeanDefinition> beanDefinitions = xmlBeanDefinitionParser.readBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            System.out.println(beanDefinition);
        }
    }
}
