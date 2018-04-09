package com.belosh.ioc.service

class UserService implements Runnable {
    private MailService mailService

    void setMailService(MailService mailService) {
        this.mailService = mailService
    }

    MailService getMailService() {
        return mailService
    }

    private int getUserCount() {
        return (int) (Math.random()*1000)
    }

    @Override
    void run() {
        while(true) {
            int numberOfUsersInSystem = getUserCount()
            mailService.sendEmail("tech@project.com", "there are " + numberOfUsersInSystem + " users in System")
        }
    }

    @Override
    String toString() {
        return "UserService{" +
                "mailService=" + mailService +
                '}'
    }
}
