package com.ttn.ecommerce.service;

import com.ttn.ecommerce.advice.PasswordDoNotMatchException;
import com.ttn.ecommerce.entity.NotificationToken;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.NotificationTokenRepository;
import com.ttn.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Service
public class NotificationTokenService {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    NotificationTokenRepository notificationTokenRepository;


    /*
    Scope of improvement:
    Figure out how to get rid of nested if-else

    currently returning appropriate string response for every situation in the service,
    controller returns ResponseEntity<String> with the string response and
    HttpStatus.OK (which shouldn't always be the case)


    Possible solutions:
    1 - Service returns appropriate ResponseEntity<String> with appropriate string response and status code,
        Controller passes that along. ( service generating ResponseEntity<> is a bad practise? )
    2-  Service throws custom exception in un-ideal situations, which will return appropriate ResponseEntity<>.
        For ideal situation return appropriate string response and controller will return ResponseEntity<> with
        string response and HTTPStatus.Ok ( appropriate for ideal conditions). ( Would require writing more code ?)

     */
    public String activateUserAccount(String token){

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
                return "Previous link has expired, a new activation mail has been sent to the registered email.";
            }
            else{
                // activate account, delete token & trigger a new mail notifying that account is active
                user.setActive(true);
                notificationTokenRepository.delete(activationToken);
                userRepository.save(user);
                emailService.sendIsActivatedMail(user);
                return "Account activated.";
            }
    }
        else {
            return "Invalid token.";
        }
    }


    public String resendActivationMail(String email){
        User user = userRepository.findUserByEmail(email);
        if(user==null){
            return "Invalid Email.";
        } else{
            // check if account is already active
            if(user.isActive()){
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

        return "Activation mail has been sent to the provided email.";
    }

    public String forgotPassword(String email){
        User user = userRepository.findUserByEmail(email);
        if(user==null){
            return "Invalid Email.";
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
            // if inactive
            else{
                return "Account is in-active. Request cannot be processed.";
            }
        }
        return "A mail has been sent to the provided email.";
    }

    public String resetPassword(String token, String password, String confirmPassword){
        NotificationToken passwordToken = notificationTokenRepository.findByToken(token);
        // checks if provided token exists
        if(passwordToken!=null){
            User user = userRepository.findUserByEmail(passwordToken.getUser().getEmail());
            // check to see whether token has expired
            // Expires after 15 min
            if(passwordToken.getCreationTime().isBefore(LocalDateTime.now().minusMinutes(15l))){
                // delete the token
                notificationTokenRepository.delete(passwordToken);
                return "Link has expired. Request cannot be processed further.";
            }
            else{
                // change password
                if(!(password.equals(confirmPassword))){
                    throw new PasswordDoNotMatchException("Passwords do not match.");
                }
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                notificationTokenRepository.delete(passwordToken);
                emailService.sendSuccessfulChangeMail(user);
                return "Password successfully changed.";
            }
        }
        else {
            return "Invalid token. Request cannot be processed further.";
        }
    }
}

