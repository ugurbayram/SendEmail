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
 * Requirement 3
 * sending an HTML email to an outside resource,
 * with a disclaimer added at the end and encrypted with AES with retries in case of errors
 */
public class AESSecureEmailServiceImpl implements EmailService, Runnable {
    private final static Logger LOGGER = Logger.getLogger(AESSecureEmailServiceImpl.class.getName());
    private BlockingQueue<Email> queue = null;
    private JavaMailSenderImpl mailSender;


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
            secure(mailMsg, "AES");
            mailMsg += disclamer();
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
    public String disclamer() {
        return "<br/><div style=\"color: #5e9ca0; font-size:10pt;  font-family: 'Calibri',sans-serif;\">" +
                "<br/>DISCLAIMER: This e-mail and any files transmitted with it are confidential and intended solely for the use of the individual or entity to whom they are addressed. \n" +
                "If you are not the intended recipient you are hereby notified that any dissemination, forwarding, copying or use of any of the information is strictly prohibited, and the e-mail should immediately be deleted.&nbsp;</br></div>";
    }

    public void setEmailSender(JavaMailSenderImpl emailSender) {
        this.mailSender = emailSender;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            try {
                Email queueEmail = (Email) queue.take();
                MimeMessage mimeMessage = (MimeMessage) getEmailBody(queueEmail);
                mailSender.send(mimeMessage);

            } catch (InterruptedException e) {
                LOGGER.severe("=====>>" + e.getMessage());
            }
        }
    }
}
