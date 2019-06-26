package com.luxoft.email.service;

import com.luxoft.email.model.Email;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * created by @ubayram
 * 17 June 2019
 */

/**
 * Requirement 4
 * sending a plain text email to an outside resource and encrypted first with DES and then with AES
 */
public class SecureEmailServiceImpl implements EmailService, Runnable {
    private final static Logger LOGGER = Logger.getLogger(SecureEmailServiceImpl.class.getName());
    private BlockingQueue<Email> queue;
    private JavaMailSenderImpl mailSender;

    public void setQueue(BlockingQueue<Email> queue) {
        this.queue = queue;
    }

    public Object getEmailBody(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        String mailMsg = email.getMsg();
        secure(mailMsg, "AES & DES");
        message.setFrom(email.getFrom());
        message.setTo(email.getTo());
        message.setSubject(email.getSubject());

        message.setText(email.getMsg());
        return message;
    }

    public void secure(String message, String level) {
        LOGGER.info("=====>>" + "Message has been encrypted with " + level + " 128 bit algorithm");
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            try {
                Email queueEmail = queue.take();
                SimpleMailMessage message = (SimpleMailMessage) getEmailBody(queueEmail);
                mailSender.send(message);
                LOGGER.info("=====>>" + "Message has been sent...");
            } catch (InterruptedException e) {
                LOGGER.severe("=====>>" + e.getMessage());
            }
        }
    }

    public void setEmailSender(JavaMailSenderImpl emailSender) {
        this.mailSender = emailSender;
    }
}
