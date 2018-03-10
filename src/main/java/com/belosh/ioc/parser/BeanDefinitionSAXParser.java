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
    private BeanDefinition beanDefinition;
    private Map<String, String> dependencies;
    private Map<String, String> refDependencies;

    public BeanDefinitionSAXParser(String path) {
        this.path = path;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equalsIgnoreCase("bean")) {
            // Initialize bean structure
            beanDefinition = new BeanDefinition();
            dependencies = new HashMap<>();
            refDependencies = new HashMap<>();
            // Set bean attributes
            beanDefinition.setId(attributes.getValue("id"));
            beanDefinition.setBeanClassName(attributes.getValue("class"));
        } else if (qName.equalsIgnoreCase("property")) {
            String name = attributes.getValue("name");
            String value = attributes.getValue("value");
            String ref = attributes.getValue("ref");
            if (value != null && !value.isEmpty()) {
                dependencies.put(name, value);
            }
            if (ref != null && !ref.isEmpty()) {
                refDependencies.put(name, ref);
            }
        } else if (qName.equalsIgnoreCase("import")) {
            String path = attributes.getValue("resource");
            readBeanDefinitions(path);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("bean")) {
            beanDefinition.setDependencies(dependencies);
            beanDefinition.setRefDependencies(refDependencies);
            beanDefinitions.add(beanDefinition);
        }
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        return readBeanDefinitions(path);
    }

    private List<BeanDefinition> readBeanDefinitions(String xmlFilePath) {
        File contextXML;
        try {
            contextXML = new File(getClass().getClassLoader().getResource(xmlFilePath).getFile());
        } catch (NullPointerException e) {
            throw new ParseXMLException("File not found in classpath", e);
        }

        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(contextXML, this);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        return beanDefinitions;
    }
}
