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
 * Requirement 1
 * Sending a plain text email to an outside resource,
 * with a disclaimer added at the end, unencrypted and no retry
 */
public class NonSecureEmailServiceImpl implements EmailService, Runnable {

    private final static Logger LOGGER = Logger.getLogger(NonSecureEmailServiceImpl.class.getName());
    private BlockingQueue<Email> queue;
    private JavaMailSenderImpl mailSender;

    public void setQueue(BlockingQueue<Email> queue) {
        this.queue = queue;
    }


    @Override
    public Object getEmailBody(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email.getFrom());
        message.setTo(email.getTo());
        message.setSubject(email.getSubject());
        message.setText(email.getMsg() + "" + disclamer());
        return message;
    }

    @Override
    public String disclamer() {
        return "\n DISCLAIMER:\nThis e-mail and any files transmitted with it are confidential and intended solely for the use of the individual or entity to whom they are addressed. " +
                "If you are not the intended recipient you are hereby notified that any dissemination, forwarding, copying or use of any of the information is strictly prohibited, and the e-mail should immediately be deleted";
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            try {
                Email queueEmail = queue.take();
                SimpleMailMessage message = (SimpleMailMessage) getEmailBody(queueEmail);
                mailSender.send(message);
            } catch (InterruptedException e) {
                LOGGER.severe("=====>>" + e.getMessage());
            }
        }
    }

    public void setEmailSender(JavaMailSenderImpl smptConf) {
        this.mailSender = smptConf;
    }


}
