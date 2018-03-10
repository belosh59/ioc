package com.belosh.ioc.service;

public class MailService {
    private String protocol;
    private String port;

    public void sendEmail(String email, String message) {
        System.out.println("Recipient: " + email);
        System.out.println("Message: " + message);
        System.out.println("SENT via " + protocol + ":" + port);
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "MailService{" +
                "protocol='" + protocol + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
