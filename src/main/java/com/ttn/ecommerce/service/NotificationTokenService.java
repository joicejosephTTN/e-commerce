package com.ttn.ecommerce.service;

import com.ttn.ecommerce.exception.*;
import com.ttn.ecommerce.entity.NotificationToken;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.NotificationTokenRepository;
import com.ttn.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class NotificationTokenService {

    Logger logger = LoggerFactory.getLogger(NotificationTokenService.class);

    @Autowired
    MessageSource messageSource;

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
                throw new UnauthorizedException(messageSource.getMessage("api.error.activationLinkExpired",null, Locale.ENGLISH));
            }
            else{
                // activate account, delete token & trigger a new mail notifying that account is active
                user.setActive(true);
                notificationTokenRepository.delete(activationToken);
                userRepository.save(user);
                emailService.sendIsActivatedMail(user);

                logger.debug("NotificationTokenService::activateUserAccount activated account");
                logger.info("NotificationTokenService::activateUserAccount execution ended.");

                return messageSource.getMessage("api.response.userAccountActivated",null, Locale.ENGLISH);
            }
    }
        else {
            logger.error("Exception occurred while activating account");
            throw new UnauthorizedException(messageSource.getMessage("api.error.invalidToken",null, Locale.ENGLISH));
        }
    }


    public String resendActivationMail(String email){

        logger.info("NotificationTokenService::resendActivationMail execution started.");

        logger.debug("NotificationTokenService::resendActivationMail resending mail");

        User user = userRepository.findUserByEmail(email);
        if(user==null){
            logger.error("Exception occurred while resending the mail");
            throw new BadRequestException(messageSource.getMessage("api.error.invalidEmail",null, Locale.ENGLISH));
        } else{
            // check if account is already active
            if(user.isActive()){
                logger.debug("NotificationTokenService::resendActivationMail account is already active");
                logger.info("NotificationTokenService::resendActivationMail execution ended.");
                return messageSource.getMessage("api.response.userAlreadyActive",null, Locale.ENGLISH);
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
        return messageSource.getMessage("api.response.resendActivation",null, Locale.ENGLISH);
    }

    public String forgotPassword(String email){
        logger.info("NotificationTokenService::forgotPassword execution started.");

        logger.debug("NotificationTokenService::forgotPassword sending mail");
        User user = userRepository.findUserByEmail(email);
        if(user==null){
            logger.error("Exception occurred while sending the mail");
            throw new BadRequestException(messageSource.getMessage("api.error.invalidEmail",null, Locale.ENGLISH));
        } else{
            // check if account is active
            if(user.isActive()){
                NotificationToken token = notificationTokenRepository.findByUser(user);
                // delete if reset password token already exists & trigger reset password mail
                if(token!=null){
                    logger.debug("NotificationTokenService::forgotPassword deleting token");
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
                throw new BadRequestException(messageSource.getMessage("api.error.accountInactive",null, Locale.ENGLISH));

            }
        }
        logger.debug("NotificationTokenService::forgotPassword mail sent");
        logger.info("NotificationTokenService::forgotPassword execution ended.");
        return messageSource.getMessage("api.response.forgotPassword",null, Locale.ENGLISH);
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
                logger.debug("NotificationTokenService::resetPassword deleting token");
                notificationTokenRepository.delete(passwordToken);
                logger.error("Exception occurred while changing the password");
                throw new UnauthorizedException(messageSource.getMessage("api.error.linkExpired",null, Locale.ENGLISH)) ;
            }
            else{
                // check if passwords match
                if(!(password.equals(confirmPassword))){
                    logger.error("Exception occurred while changing the password");
                    throw new BadRequestException(messageSource.getMessage("api.error.passwordDoNotMatch",null, Locale.ENGLISH));
                }
                // change password
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                logger.debug("NotificationTokenService::resetPassword deleting token");
                notificationTokenRepository.delete(passwordToken);
                emailService.sendSuccessfulChangeMail(user);
                logger.info("NotificationTokenService::resetPassword execution ended.");
                return messageSource.getMessage("api.response.updateSuccess",null, Locale.ENGLISH);
            }
        }
        else {
            logger.error("Exception occurred while changing the password");
            throw new UnauthorizedException(messageSource.getMessage("api.error.invalidToken",null, Locale.ENGLISH));
        }
    }
}

