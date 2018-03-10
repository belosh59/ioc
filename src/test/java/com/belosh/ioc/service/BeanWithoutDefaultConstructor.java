package com.belosh.ioc.service;

public class BeanWithoutDefaultConstructor {
    String testField;

    public BeanWithoutDefaultConstructor(String testField) {
        this.testField = testField;
    }

    public String getTestField() {
        return testField;
    }

    public void setTestField(String testField) {
        this.testField = testField;
    }
}
