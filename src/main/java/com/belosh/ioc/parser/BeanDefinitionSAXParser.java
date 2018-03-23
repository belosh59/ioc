package com.belosh.ioc.parser;

import com.belosh.ioc.exceptions.ParseXMLException;
import org.xml.sax.helpers.DefaultHandler;import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefinitionSAXParser extends DefaultHandler implements BeanDefinitionReader {
    private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    private String path;
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();

    public BeanDefinitionSAXParser(String path) {
        this.path = path;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equalsIgnoreCase("bean")) {
            // Initialize bean structure
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setDependencies(new HashMap<String, String>());
            beanDefinition.setRefDependencies(new HashMap<String, String>());

            // Set bean attributes
            beanDefinition.setId(attributes.getValue("id"));
            beanDefinition.setBeanClassName(attributes.getValue("class"));
            beanDefinitions.add(beanDefinition);
        } else if (qName.equalsIgnoreCase("property")) {
            String name = attributes.getValue("name");
            String value = attributes.getValue("value");
            String ref = attributes.getValue("ref");
            BeanDefinition lastBeanDefinition = beanDefinitions.get(beanDefinitions.size()-1);

            if (value != null && !value.isEmpty()) {
                lastBeanDefinition.getDependencies().put(name, value);
            }

            if (ref != null && !ref.isEmpty()) {
                lastBeanDefinition.getRefDependencies().put(name, ref);
            }
        } else if (qName.equalsIgnoreCase("import")) {
            String path = attributes.getValue("resource");
            readBeanDefinitions(path);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        return readBeanDefinitions(path);
    }

    private List<BeanDefinition> readBeanDefinitions(String xmlFilePath) {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
        if (resource == null) {
            throw new ParseXMLException("File " + xmlFilePath + " not found in classpath");
        }

        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(resource, this);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Could not parse file: " + xmlFilePath, e);
        }

        return beanDefinitions;
    }
}
