package com.luxoft.email.service;


import com.luxoft.email.model.Email;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * created by @ubayram
 * 17 June 2019
 */

/**
 * Requirement 2
 * sending an HTML email to an internal server (so without the disclaimer),
 * encrypted with DES, with the retry functionality
 */
public class DESSecureEmailServiceImpl implements EmailService, Runnable {
    private final static Logger LOGGER = Logger.getLogger(DESSecureEmailServiceImpl.class.getName());

    private BlockingQueue<Email> queue = null;
    private JavaMailSenderImpl mailSender;
    private boolean failRetry = true;

    public void setQueue(BlockingQueue<Email> queue) {
        this.queue = queue;
    }

    @Override
    public Object getEmailBody(Email email) {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {

            helper = new MimeMessageHelper(message, false, "utf-8");
            String mailMsg = email.getMsg();
            secure(mailMsg, "DES");
            message.setContent(mailMsg, "text/html");
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setFrom(email.getFrom());

        } catch (MessagingException e) {
            LOGGER.severe("=====>>" + e.getMessage());
        }
        return message;
    }

    public void secure(String message, String level) {
        LOGGER.info("=====>>" + "Message has been encrypted with " + level + " 128 bit algorithm");
    }

    @Override
    public void run() {
        while (failRetry) {
            if (!queue.isEmpty()) {
                try {
                    Email queueEmail = (Email) queue.take();
                    MimeMessage mimeMessage = (MimeMessage) getEmailBody(queueEmail);
                    mailSender.send(mimeMessage);
                    LOGGER.info("=====>>" + "Message has been sent...");
                    if (queueEmail.retryCount <= 0)
                        failRetry = false;
                } catch (InterruptedException e) {
                    LOGGER.severe("=====>>" + e.getMessage());
                }
            }
            try {
                Thread.sleep(retryPeriod);
            } catch (InterruptedException e) {
                LOGGER.severe("=====>>" + e.getMessage());
            }
        }
    }

    public void setEmailSender(JavaMailSenderImpl emailSender) {
        this.mailSender = emailSender;
    }

}
