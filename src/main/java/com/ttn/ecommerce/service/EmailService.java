package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.NotificationToken;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.NotificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    NotificationTokenRepository notificationTokenRepository;

    private static final String ACTIVATION_SUBJECT = "Account Activation | Dummy Ecommerce Application";
    private static final String RESET_SUBJECT = "Password Reset Request | Dummy Ecommerce Application";


    @Async
    public void sendEmail(String toEmail, String body, String subject) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("joice.joseph3198@gmail.com");
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        javaMailSender.send(mailMessage);
        // add logger
    }


    // method to trigger an activation mail with token
    public void sendActivationMail(User user){
        NotificationToken activationToken = new NotificationToken(user);
        notificationTokenRepository.save(activationToken);

        String emailBody = "Hi " + user.getFirstName() +", " + "\n\n" +
                "We're excited to have you get started. First, you need to activate your account " +
                "Just go to the link mentioned below to activate your account. The link is valid for 3 hours." +
                "\n\n"+"http://localhost:8080/api/activate_account?token="+ activationToken.getToken() + "\n\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, ACTIVATION_SUBJECT);

    }

    // method to trigger a mail to notify that account is activated
    public void sendIsActivatedMail(User user){
        String emailBody = "Hi " + user.getFirstName() +", " + "\n" +
                "Welcome to 'Dummy Ecommerce Application', your account has been activated. " +
                "\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, ACTIVATION_SUBJECT);
    }


    // method to trigger a mail to reset the password
    public void sendForgotPasswordMail(User user){

        NotificationToken passwordToken = new NotificationToken(user);
        notificationTokenRepository.save(passwordToken);

        String emailBody = "Hi " + user.getFirstName() +", " + "\n" +
                "A request has been received to change the password for your account. " +
                "Please go the link mentioned below to change your password. " +
                "\n" + "http://localhost:8080/api/reset_password?token="+ passwordToken.getToken() + "\n\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, RESET_SUBJECT);
    }

    // method to trigger a mail to notify successful password change
    public void sendSuccessfulChangeMail(User user) {
        String emailBody = "Hi " + user.getFirstName() +", " + "\n" +
                "Your password has been changed successfully. Please use your new password to access your account." +
                "\n" +
                "- Team 'Dummy Ecommerce Application' ";

        sendEmail(user.getEmail(), emailBody, RESET_SUBJECT);
    }
}
