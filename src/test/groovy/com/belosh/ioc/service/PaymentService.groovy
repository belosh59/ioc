package com.belosh.ioc.service

class PaymentService {
    private MailService mailService
    private int maxAmount

    void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount
    }

    void setMailService(MailService mailService) {
        this.mailService = mailService
    }

    void pay(String from, String to, double account) {
        mailService.sendEmail("from", "withdrawal successful")
        mailService.sendEmail("to", "credit successful")
    }

    MailService getMailService() {
        return mailService
    }
}
