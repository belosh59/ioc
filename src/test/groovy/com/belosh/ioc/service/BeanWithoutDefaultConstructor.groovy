package com.belosh.ioc.service

class BeanWithoutDefaultConstructor {
    String testField

    BeanWithoutDefaultConstructor(String testField) {
        this.testField = testField
    }

    String getTestField() {
        return testField
    }

    void setTestField(String testField) {
        this.testField = testField
    }
}
