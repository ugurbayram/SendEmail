package com.luxoft.email.utils;

import com.luxoft.email.model.Email;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * created by @ubayram
 * 17 June 2019
 */
public class EmailUtils {

    private final static Logger LOGGER = Logger.getLogger(EmailUtils.class.getName());

    public static Email getEmail(int taskNum, Properties properties) {
        return new Email(properties.getProperty("EMAIL_FROM"),
                properties.getProperty("EMAIL_TO"),
                properties.getProperty("EMAIL_SUBJECT_TASK" + taskNum),
                properties.getProperty("EMAIL_MSG_TASK" + taskNum),
                Integer.valueOf(properties.getProperty("MAX_RETRY_COUNT")));
    }

    public static String getMailContent(MimeMessage message) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String strContent = "";
        try {
            message.writeTo(out);
            strContent = out.toString();
        } catch (IOException | MessagingException e) {
            LOGGER.severe("=====>>" + e.getMessage());
        }
        return strContent;
    }


}
