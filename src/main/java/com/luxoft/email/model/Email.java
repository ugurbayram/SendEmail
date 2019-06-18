package com.luxoft.email.model;

/**
 * created by @ubayram
 * 17 June 2019
 */
public class Email {

    private String from;
    private String to;
    private String subject;
    private String msg;
    public int retryCount;

    public Email(String from, String to, String subject, String msg, int retryCount) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.msg = msg;
        this.retryCount = retryCount;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getMsg() {
        return msg;
    }
}
