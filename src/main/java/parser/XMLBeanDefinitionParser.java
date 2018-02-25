package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLBeanDefinitionParser implements BeanDefinitionReader{
    private String path;

    public List<BeanDefinition> readBeanDefinitions() {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File contextXML = new File(classLoader.getResource(path).getFile());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(contextXML);
            document.getDocumentElement().normalize();

            NodeList beans = document.getElementsByTagName("bean");
            for (int i = 0; i < beans.getLength(); i++){
                Node bean = beans.item(i);
                if (bean.getNodeType() == Node.ELEMENT_NODE) {
                    // New Bean initialisation
                    BeanDefinition beanDefinition = new BeanDefinition();
                    Map<String, String> dependencies = new HashMap<>();
                    Map<String, String> refDependencies = new HashMap<>();
                    //Process root
                    Element elementBean = (Element) bean;
                    beanDefinition.setId(elementBean.getAttribute("id"));
                    beanDefinition.setBeanClassName(elementBean.getAttribute("class"));
                    NodeList beanProps = bean.getChildNodes();
                    //Process properties
                    for (int j = 0; j < beanProps.getLength(); j++) {
                        Node property = beanProps.item(j);
                        if (property.getNodeType() == Node.ELEMENT_NODE) {
                            Element elementProperty = (Element) property;
                            String name = elementProperty.getAttribute("name");
                            String value = elementProperty.getAttribute("value");
                            String ref = elementProperty.getAttribute("ref");
                            if (!value.equals("")) {
                                dependencies.put(name, value);
                            }
                            if (!ref.equals("")) {
                                refDependencies.put(name, ref);
                            }
                        }
                    }
                    beanDefinition.setDependencies(dependencies);
                    beanDefinition.setRefDependencies(refDependencies);
                    beanDefinitions.add(beanDefinition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanDefinitions;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
