package com.belosh.ioc.service


import com.belosh.ioc.processor.PostConstruct

class MailService {
    private String protocol
    private String port

    private String postProcessorBeforeCfg
    private boolean initExecuted
    private String postProcessorAfterCfg

    @PostConstruct
    private void init() {
        initExecuted = true
    }

    void sendEmail(String email, String message) {
        System.out.println("Recipient: " + email)
        System.out.println("Message: " + message)
        System.out.println("SENT via " + protocol + ":" + port)
    }

    void setProtocol(String protocol) {
        this.protocol = protocol
    }

    void setPort(String port) {
        this.port = port
    }

    String getProtocol() {
        return protocol
    }

    String getPort() {
        return port
    }

    boolean isInitExecuted() {
        return initExecuted
    }

    void setPostProcessorBeforeCfg(String postProcessorBeforeCfg) {
        this.postProcessorBeforeCfg = postProcessorBeforeCfg
    }

    void setPostProcessorAfterCfg(String postProcessorAfterCfg) {
        this.postProcessorAfterCfg = postProcessorAfterCfg
    }

    @Override
    String toString() {
        return "MailService{" +
                "protocol='" + protocol + '\'' +
                ", port='" + port + '\'' +
                '}'
    }
}
