package com.belosh.ioc.parser;

import com.belosh.ioc.exceptions.ParseXMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefinitionDOMParser implements BeanDefinitionReader{
    private String path;
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();

    public BeanDefinitionDOMParser(String path) {
        this.path = path;
    }

    public List<BeanDefinition> readBeanDefinitions() {
        loadAndParseXML(path);
        return beanDefinitions;
    }

    private void loadAndParseXML(String xmlFilePath) {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
        if (resource == null) {
            throw new ParseXMLException("File " + xmlFilePath + " not found in classpath");
        }
        domParseContextXML(resource);
    }

    private void domParseContextXML(InputStream inputStream) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList imports = document.getElementsByTagName("import");
            for (int i = 0; i < imports.getLength(); i++) {
                Node importXML = imports.item(i);
                //Process root
                Element elementBean = (Element) importXML;
                loadAndParseXML(elementBean.getAttribute("resource"));
            }

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
                            if (!value.isEmpty()) {
                                dependencies.put(name, value);
                            }
                            if (!ref.isEmpty()) {
                                refDependencies.put(name, ref);
                            }
                        }
                    }
                    beanDefinition.setDependencies(dependencies);
                    beanDefinition.setRefDependencies(refDependencies);
                    beanDefinitions.add(beanDefinition);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException("Unable to parse xml from stream");
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Cannot configure DOM Parser");
        }
    }
}
