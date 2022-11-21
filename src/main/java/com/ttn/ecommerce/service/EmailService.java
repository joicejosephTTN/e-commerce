package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.NotificationToken;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.NotificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    NotificationTokenRepository notificationTokenRepository;

    private static final String ACTIVATION_SUBJECT = "Account Activation | Dummy Ecommerce Application";
    private static final String DEACTIVATION_SUBJECT = "Account Deactivation | Dummy Ecommerce Application";
    private static final String RESET_SUBJECT = "Password Reset Request | Dummy Ecommerce Application";
    private static final String ACCOUNT_LOCKED_SUBJECT = "Suspicious Account Activity | Dummy Ecommerce Application";


    @Async
    public void sendEmail(String toEmail, String body, String subject) {
        logger.info("EmailService::sendMail execution started.");

        logger.debug("EmailService::sendMail configuring email details");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("joice.joseph3198@gmail.com");
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        javaMailSender.send(mailMessage);
        logger.info("EmailService::sendMail execution ended.");
        // add logger
    }


    // method to trigger an activation mail with token
    public void sendActivationMail(User user){
        logger.info("EmailService::sendActivationMail execution started.");

        logger.debug("EmailService::sendActivationMail generating token, composing email to send");
        NotificationToken activationToken = new NotificationToken(user);
        notificationTokenRepository.save(activationToken);

        String emailBody = "Hi " + user.getFirstName() +", " + "\n\n" +
                "We're excited to have you get started. First, you need to activate your account " +
                "Just go to the link mentioned below to activate your account. The link is valid for 3 hours." +
                "\n\n"+"http://localhost:8080/api/activate_account?token="+ activationToken.getToken() + "\n\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, ACTIVATION_SUBJECT);
        logger.info("EmailService::sendActivationMail execution ended.");

    }

    // method to trigger a mail to notify that account is activated
    public void sendIsActivatedMail(User user){
        logger.info("EmailService::sendIsActivatedMail execution started.");

        logger.debug("EmailService::sendIsActivatedMail composing email to send");
        String emailBody = "Hi " + user.getFirstName() +", " + "\n" +
                "Welcome to 'Dummy Ecommerce Application', your account has been activated. " +
                "\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, ACTIVATION_SUBJECT);
        logger.info("EmailService::sendIsActivatedMail execution ended.");
    }


    // method to trigger a mail to reset the password
    public void sendForgotPasswordMail(User user){
        logger.info("EmailService::sendForgotPasswordMail execution started.");

        logger.debug("EmailService::sendForgotPasswordMail generating token, composing email to send");

        NotificationToken passwordToken = new NotificationToken(user);
        notificationTokenRepository.save(passwordToken);

        String emailBody = "Hi " + user.getFirstName() +", " + "\n" +
                "A request has been received to change the password for your account. " +
                "Please go the link mentioned below to change your password. The link will expire in 15min." +
                "\n" + "http://localhost:8080/api/reset_password?token="+ passwordToken.getToken() + "\n\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, RESET_SUBJECT);
        logger.info("EmailService::sendForgotPasswordMail execution ended.");
    }

    // method to trigger a mail to notify successful password change
    public void sendSuccessfulChangeMail(User user) {
        logger.info("EmailService::sendSuccessfulChangeMail execution started.");

        logger.debug("EmailService::sendSuccessfulChangeMail composing email to send");
        String emailBody = "Hi " + user.getFirstName() +", " + "\n" +
                "Your password has been changed successfully. Please use your new password to access your account." +
                "\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, RESET_SUBJECT);
        logger.info("EmailService::sendSuccessfulChangeMail execution ended.");
    }

    public void sendAwaitingApprovalMail(User user){
        logger.info("EmailService::sendAwaitingApprovalMail execution started.");

        logger.debug("EmailService::sendAwaitingApprovalMail composing email to send");

        String emailBody = "Hi " + user.getFirstName() +", " + "\n\n" +
                "We're excited to have you get started. Your account is currently pending approval. " +
                "\n\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, ACTIVATION_SUBJECT);
        logger.info("EmailService::sendAwaitingApprovalMail execution ended.");

    }

    public void sendAccountLockedMail(User user){
        logger.info("EmailService::sendAccountLockedMail execution started.");

        logger.debug("EmailService::sendAccountLockedMail composing email to send");

        String emailBody = "Hi " + user.getFirstName() +", " + "\n\n" +
                "We've detected suspicious activity within your account. Your account has been temporarily put on lock. " +
                "Please contact technical support team if you think was a mistake." +
                "\n\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, ACCOUNT_LOCKED_SUBJECT);
        logger.info("EmailService::sendAccountLockedMail execution ended.");

    }

    public void sendDeActivatedMail(User user) {
        logger.info("EmailService::sendDeActivatedMail execution started.");

        logger.debug("EmailService::sendDeActivatedMail composing email to send");

        String emailBody = "Hi " + user.getFirstName() +", " + "\n\n" +
                "Your account has been temporarily deactivated. " +
                "Please contact technical support team if you think was a mistake." +
                "\n\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, DEACTIVATION_SUBJECT);
        logger.info("EmailService::sendDeActivatedMail execution ended.");
    }
}
