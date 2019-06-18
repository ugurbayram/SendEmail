package com.luxoft.email;

import com.luxoft.email.model.Email;
import com.luxoft.email.service.AESSecureEmailServiceImpl;
import com.luxoft.email.service.DESSecureEmailServiceImpl;
import com.luxoft.email.service.NonSecureEmailServiceImpl;
import com.luxoft.email.service.SecureEmailServiceImpl;
import com.luxoft.email.utils.EmailUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * created by @ubayram
 * 17 June 2019
 */

public class Main {

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static ApplicationContext context = null;
    private JavaMailSenderImpl mailSender = null;

    public Main() {
        context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
    }

    public static void main(String[] args) {
        Main main = new Main();

        // Channel to monitor sender's inbox.
        DirectChannel inputChannel = context.getBean("receiveChannel", DirectChannel.class);

        //TASK#1 Initialize thread to send email to outsider with non-encryption
        NonSecureEmailServiceImpl nonSecureEmailService = (NonSecureEmailServiceImpl) context.getBean("NonSecureEmail");
        BlockingQueue nonSecureEmailQueue = new ArrayBlockingQueue<Email>(3);
        try {
            Email emailNon = EmailUtils.getEmail(1);
            nonSecureEmailQueue.put(emailNon);
        } catch (InterruptedException e) {
            LOGGER.severe("=====>>" + e.getMessage());
        }
        nonSecureEmailService.setQueue(nonSecureEmailQueue);
        Thread nonSecureEmailThread = new Thread(nonSecureEmailService);
        nonSecureEmailThread.start();

        //TASK#2 Initialize thread to send email to insider with DES Encryption
        DESSecureEmailServiceImpl desSecureEmailService = (DESSecureEmailServiceImpl) context.getBean("DESSecureEmail");
        BlockingQueue desSecureEmailQueue = new ArrayBlockingQueue<Email>(3);
        Email emailDES = EmailUtils.getEmail(2);
        try {
            desSecureEmailQueue.put(emailDES);
        } catch (InterruptedException e) {
            LOGGER.severe("=====>>" + e.getMessage());
        }
        desSecureEmailService.setQueue(desSecureEmailQueue);
        inputChannel.subscribe(message -> {
            MimeMessage receivedMessage = (MimeMessage) message.getPayload();
            try {
                if (receivedMessage.getSubject().indexOf("Delivery Status Notification (Failure)") > -1 && emailDES.retryCount > 0) {
                    if (EmailUtils.getMailContent(receivedMessage).indexOf(emailDES.getSubject()) > -1) {
                        LOGGER.info("=====>>" + receivedMessage.getSubject() + " for "+emailDES.getSubject());
                        emailDES.retryCount--;
                        desSecureEmailQueue.put(emailDES);
                    }
                }
            } catch (MessagingException e) {
                LOGGER.severe("=====>>" + e.getMessage());
            } catch (InterruptedException e) {
                LOGGER.severe("=====>>" + e.getMessage());
            }
        });
        Thread desSecureEmailThread = new Thread(desSecureEmailService);
        desSecureEmailThread.start();

        //TASK#3 Initialize thread to send email to outsider AES Encryption
        AESSecureEmailServiceImpl aesSecureEmailService = (AESSecureEmailServiceImpl) context.getBean("AESSecureEmail");
        BlockingQueue aesSecureEmailQueue = new ArrayBlockingQueue<Email>(3);
        Email emailAES = EmailUtils.getEmail(3);
        try {
            aesSecureEmailQueue.put(emailAES);
        } catch (InterruptedException e) {
            LOGGER.severe("=====>>" + e.getMessage());
        }
        aesSecureEmailService.setQueue(aesSecureEmailQueue);
        inputChannel.subscribe(message -> {
            MimeMessage receivedMessage = (MimeMessage) message.getPayload();
            try {
                if (receivedMessage.getSubject().indexOf("Delivery Status Notification (Failure)") > -1 && emailAES.retryCount > 0) {
                    if (EmailUtils.getMailContent(receivedMessage).indexOf(emailAES.getSubject()) > -1) {
                        LOGGER.info("=====>>" + receivedMessage.getSubject()+" for "+ emailAES.getSubject());
                        emailAES.retryCount--;
                        aesSecureEmailQueue.put(emailAES);
                    }
                }
            } catch (MessagingException e) {
                LOGGER.severe("=====>>" + e.getMessage());
            } catch (InterruptedException e) {
                LOGGER.severe("=====>>" + e.getMessage());
            }
        });
        Thread aesSecureEmailThread = new Thread(aesSecureEmailService);
        aesSecureEmailThread.start();


        //TASK#4 Initialize thread to send email to outsider with AES and DES Secure Encryption
        SecureEmailServiceImpl secureEmailService = (SecureEmailServiceImpl) context.getBean("SecureEmail");
        BlockingQueue secureEmailQueue = new ArrayBlockingQueue<Email>(3);
        try {
            Email emailSec = EmailUtils.getEmail(4);
            secureEmailQueue.put(emailSec);
        } catch (InterruptedException e) {
            LOGGER.severe("=====>>" + e.getMessage());
        }
        secureEmailService.setQueue(secureEmailQueue);
        Thread secureEmailThread = new Thread(secureEmailService);
        secureEmailThread.start();

        try {
            nonSecureEmailThread.join();
            aesSecureEmailThread.join();
            desSecureEmailThread.join();
            secureEmailThread.join();
        } catch (InterruptedException e) {
            LOGGER.severe("=====>>" + e.getMessage());
        }
    }
}
