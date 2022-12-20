package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.NotificationToken;
import com.ttn.ecommerce.entity.Product;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.model.ProductResponseDTO;
import com.ttn.ecommerce.repository.NotificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    MessageSource messageSource;
    Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    NotificationTokenRepository notificationTokenRepository;

    // method to compose and send an email
    @Async
    public void sendEmail(String toEmail, String body, String subject) {
        logger.info("EmailService::sendMail execution started.");

        logger.debug("EmailService::sendMail configuring email details");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("joice.joseph@tothenew.com");
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        javaMailSender.send(mailMessage);
        logger.info("EmailService::sendMail execution ended.");

    }

    // method to trigger an activation mail with token
    public void sendActivationMail(User user){
        logger.info("EmailService::sendActivationMail execution started.");

        logger.debug("EmailService::sendActivationMail generating token, composing email to send");
        NotificationToken activationToken = new NotificationToken(user);
        notificationTokenRepository.save(activationToken);

        String subject = messageSource.getMessage("api.email.activationSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.activationMail",null, Locale.ENGLISH);
        String link = messageSource.getMessage("api.email.siteUrl",null, Locale.ENGLISH);

        body = body.replace("[[name]]", user.getFirstName());
        body = body.replace("[[URL]]",link +"/activateAccount?token="+ activationToken.getToken());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendActivationMail execution ended.");

    }

    // method to trigger a mail to notify that account is activated
    public void sendIsActivatedMail(User user){
        logger.info("EmailService::sendIsActivatedMail execution started.");

        logger.debug("EmailService::sendIsActivatedMail composing email to send");

        String subject = messageSource.getMessage("api.email.activationSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.isActivatedMail",null, Locale.ENGLISH);
        body = body.replace("[[name]]", user.getFirstName());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendIsActivatedMail execution ended.");
    }

    // method to trigger a mail to reset the password
    public void sendForgotPasswordMail(User user){
        logger.info("EmailService::sendForgotPasswordMail execution started.");

        logger.debug("EmailService::sendForgotPasswordMail generating token, composing email to send");

        NotificationToken passwordToken = new NotificationToken(user);
        notificationTokenRepository.save(passwordToken);

        String subject = messageSource.getMessage("api.email.resetSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.forgotPasswordMail",null, Locale.ENGLISH);
        String link = messageSource.getMessage("api.email.siteUrl",null, Locale.ENGLISH);

        body = body.replace("[[name]]", user.getFirstName());
        body = body.replace("[[URL]]",link +"/resetPassword?token="+ passwordToken.getToken());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendForgotPasswordMail execution ended.");
    }

    // method to trigger a mail to notify successful password change
    public void sendSuccessfulChangeMail(User user) {
        logger.info("EmailService::sendSuccessfulChangeMail execution started.");

        logger.debug("EmailService::sendSuccessfulChangeMail composing email to send");
        String subject = messageSource.getMessage("api.email.resetSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.successfulChangeMail",null, Locale.ENGLISH);
        body = body.replace("[[name]]", user.getFirstName());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendSuccessfulChangeMail execution ended.");
    }

    // method to trigger a mail to notify the account activation is awaiting approval
    public void sendAwaitingApprovalMail(User user){
        logger.info("EmailService::sendAwaitingApprovalMail execution started.");

        logger.debug("EmailService::sendAwaitingApprovalMail composing email to send");

        String subject = messageSource.getMessage("api.email.activationSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.awaitingApprovalMail",null, Locale.ENGLISH);
        body = body.replace("[[name]]", user.getFirstName());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendAwaitingApprovalMail execution ended.");

    }


    // method to trigger a mail to notify the account has been locked
    public void sendAccountLockedMail(User user){
        logger.info("EmailService::sendAccountLockedMail execution started.");

        logger.debug("EmailService::sendAccountLockedMail composing email to send");

        String subject = messageSource.getMessage("api.email.accountLockedSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.accountLockedMail",null, Locale.ENGLISH);
        body = body.replace("[[name]]", user.getFirstName());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendAccountLockedMail execution ended.");

    }

    // method to trigger a mail to notify that account has been deactivated
    public void sendDeActivatedMail(User user) {
        logger.info("EmailService::sendDeActivatedMail execution started.");

        logger.debug("EmailService::sendDeActivatedMail composing email to send");

        String subject = messageSource.getMessage("api.email.deactivationSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.accountDeactivatedMail",null, Locale.ENGLISH);
        body = body.replace("[[name]]", user.getFirstName());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendDeActivatedMail execution ended.");
    }

    // method to trigger a mail to notify that product has been created
    public void sendNewProductMail(Product product) {
        logger.info("EmailService::sendNewProductMail execution started.");

        logger.debug("EmailService::sendNewProductMail composing email to send");
        String subject = messageSource.getMessage("api.email.productSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.newProductAddedMail",null, Locale.ENGLISH);
        body = body.replace("[[name]]", "Admin");
        body = body.replace("[[details]]", product.toString());

        sendEmail("tarunsingh021@gmail.com", body, subject);
        logger.info("EmailService::sendNewProductMail execution ended.");
    }

    // method to trigger a mail to notify that product has been activated
    public void sendProductActivationMail(ProductResponseDTO product,User user) {
        logger.info("EmailService::sendProductActivationMail execution started.");

        logger.debug("EmailService::sendProductActivationMail composing email to send");
        String subject = messageSource.getMessage("api.email.productSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.productActivationMail",null, Locale.ENGLISH);
        body = body.replace("[[name]]", user.getFirstName());
        body = body.replace("[[details]]", product.toString());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendProductActivationMail execution ended.");
    }

    // method to trigger a mail to notify that product has been deactivated
    public void sendProductDeactivationMail(ProductResponseDTO product,User user) {
        logger.info("EmailService::sendProductDeactivationMail execution started.");

        logger.debug("EmailService::sendProductDeactivationMail composing email to send");
        String subject = messageSource.getMessage("api.email.productSubject",null,Locale.ENGLISH);
        String body = messageSource.getMessage("api.email.productDeactivationMail",null, Locale.ENGLISH);
        body = body.replace("[[name]]", user.getFirstName());
        body = body.replace("[[details]]", product.toString());

        sendEmail(user.getEmail(), body, subject);
        logger.info("EmailService::sendProductDeactivationMail execution ended.");
    }


}
