package com.belosh.ioc.service;

public class PaymentService {
    private MailService mailService;
    private int maxAmount;

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void pay(String from, String to, double account) {
        mailService.sendEmail("from", "withdrawal successful");
        mailService.sendEmail("to", "credit successful");
    }

    public MailService getMailService() {
        return mailService;
    }
}
