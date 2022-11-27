package com.ttn.ecommerce.service;

import com.ttn.ecommerce.exception.AccountInActiveException;
import com.ttn.ecommerce.exception.InvalidTokenException;
import com.ttn.ecommerce.exception.LinkExpiredException;
import com.ttn.ecommerce.exception.PasswordDoNotMatchException;
import com.ttn.ecommerce.entity.NotificationToken;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.NotificationTokenRepository;
import com.ttn.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class NotificationTokenService {

    Logger logger = LoggerFactory.getLogger(NotificationTokenService.class);

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationTokenRepository notificationTokenRepository;


    public String activateUserAccount(String token){

        logger.info("NotificationTokenService::activateUserAccount execution started.");

        logger.debug("NotificationTokenService::activateUserAccount activating account");

        NotificationToken activationToken = notificationTokenRepository.findByToken(token);
        // checks if provided token exists
        if(activationToken!=null){
            User user = userRepository.findUserByEmail(activationToken.getUser().getEmail());
            // check to see whether token has expired
            // Expires after 3hrs
            if(activationToken.getCreationTime().isBefore(LocalDateTime.now().minusMinutes(3*60l))){
                // delete the token and trigger a new mail
                notificationTokenRepository.delete(activationToken);
                emailService.sendActivationMail(user);
                logger.error("Exception occurred while activating account");
                throw new LinkExpiredException("The link you followed has expired, " +
                        "a new activation mail has been sent to the registered email.\"");
            }
            else{
                // activate account, delete token & trigger a new mail notifying that account is active
                user.setActive(true);
                notificationTokenRepository.delete(activationToken);
                userRepository.save(user);
                emailService.sendIsActivatedMail(user);

                logger.debug("NotificationTokenService::activateUserAccount activated account");
                logger.info("NotificationTokenService::activateUserAccount execution ended.");

                return "Account activated.";
            }
    }
        else {
            logger.error("Exception occurred while activating account");
            throw new InvalidTokenException("Invalid token.") ;
        }
    }


    public String resendActivationMail(String email){

        logger.info("NotificationTokenService::resendActivationMail execution started.");

        logger.debug("NotificationTokenService::resendActivationMail resending mail");

        User user = userRepository.findUserByEmail(email);
        if(user==null){
            logger.error("Exception occurred while resending the mail");
            throw new BadCredentialsException("Invalid Email.");
        } else{
            // check if account is already active
            if(user.isActive()){
                logger.debug("NotificationTokenService::resendActivationMail account is already active");
                logger.info("NotificationTokenService::resendActivationMail execution ended.");
                return "Account is already active.";
            }
            // if inactive, check if there is an existing activation token
            else{

                NotificationToken token = notificationTokenRepository.findByUser(user);
                // delete if activation token already exists & trigger activation mail
                if(token!=null){
                    notificationTokenRepository.delete(token);
                    emailService.sendActivationMail(user);
                }
                // trigger activation mail
                else {
                    emailService.sendActivationMail(user);
                }
            }
        }
        logger.debug("NotificationTokenService::resendActivationMail mail sent.");
        logger.info("NotificationTokenService::resendActivationMail execution ended.");
        return "Activation mail has been sent to the provided email.";
    }

    public String forgotPassword(String email){
        logger.info("NotificationTokenService::forgotPassword execution started.");

        logger.debug("NotificationTokenService::forgotPassword sending mail");
        User user = userRepository.findUserByEmail(email);
        if(user==null){
            logger.error("Exception occurred while sending the mail");
            throw new BadCredentialsException("Invalid Email.");
        } else{
            // check if account is active
            if(user.isActive()){
                NotificationToken token = notificationTokenRepository.findByUser(user);
                // delete if reset password token already exists & trigger reset password mail
                if(token!=null){
                    notificationTokenRepository.delete(token);
                    emailService.sendForgotPasswordMail(user);
                }
                // trigger reset password mail
                else {
                    emailService.sendForgotPasswordMail(user);
                }
            }
            // in case account is inactive
            else{
                logger.error("Exception occurred while sending the mail");
                throw new AccountInActiveException("Account is in-active. Request cannot be processed.");

            }
        }
        logger.debug("NotificationTokenService::forgotPassword mail sent");
        logger.info("NotificationTokenService::forgotPassword execution ended.");
        return "A mail has been sent to the provided email.";
    }

    public String resetPassword(String token, String password, String confirmPassword){

        logger.info("NotificationTokenService::resetPassword execution started.");

        logger.debug("NotificationTokenService::resetPassword changing the password");
        NotificationToken passwordToken = notificationTokenRepository.findByToken(token);
        // checks if provided token exists
        if(passwordToken!=null){
            User user = userRepository.findUserByEmail(passwordToken.getUser().getEmail());
            // check to see whether token has expired
            // Expires after 15 min
            if(passwordToken.getCreationTime().isBefore(LocalDateTime.now().minusMinutes(15l))){
                // delete the token
                notificationTokenRepository.delete(passwordToken);
                logger.error("Exception occurred while changing the password");
                throw new LinkExpiredException("Link has expired. Request cannot be processed further.") ;
            }
            else{
                // check if passwords match
                if(!(password.equals(confirmPassword))){
                    logger.error("Exception occurred while changing the password");
                    throw new PasswordDoNotMatchException("Passwords do not match.");
                }
                // change password
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                notificationTokenRepository.delete(passwordToken);
                emailService.sendSuccessfulChangeMail(user);
                logger.info("NotificationTokenService::resetPassword execution ended.");
                return "Password successfully changed.";
            }
        }
        else {
            logger.error("Exception occurred while changing the password");
            throw new InvalidTokenException("Invalid token. Request cannot be processed further.");
        }
    }
}

