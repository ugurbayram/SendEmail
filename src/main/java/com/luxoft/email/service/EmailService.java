package com.luxoft.email.service;

import com.luxoft.email.model.Email;

import java.util.logging.Logger;

/**
 * created by @ubayram
 * 17 June 2019
 */


public interface EmailService {

    final static Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    Object getEmailBody(Email email);

    /**
     * Default method to set customized encryption algorithm. (AES, DES, AES&DES)
     */
    default void secure(String message, String level) {
        LOGGER.info("Encryption is not required!");
    }

    /**
     * Default method to set customized Disclaimer (Plain Text, HTML)
     */
    default String disclamer() {
        LOGGER.info(" Disclamer is not required");
        return "";
    }

}
