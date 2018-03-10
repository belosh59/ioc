package com.belosh.ioc.exceptions;

public class BeanInstantiationException extends RuntimeException {
    public BeanInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
