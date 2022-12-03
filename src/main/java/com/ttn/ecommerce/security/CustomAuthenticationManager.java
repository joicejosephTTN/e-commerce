package com.ttn.ecommerce.security;

import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.UserRepository;
import com.ttn.ecommerce.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountLockedException;
import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    Logger logger = LoggerFactory.getLogger(CustomAuthenticationManager.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        logger.info("CustomAuthenticationManager::authenticate execution started.");
        logger.debug("CustomAuthenticationManager::authenticate authenticating credentials, generating token");

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findUserByEmail(username);

        if(user==null){
            logger.error("Exception occurred while authenticating");
            throw new UsernameNotFoundException("Invalid credentials");
        }
        if(user.isLocked() || user.isActive() == false ){
            logger.error("Exception occurred while authenticating");
            throw new BadCredentialsException("Account is locked or inactive"); // change the exception
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.debug("CustomAuthenticationManager::authenticate invalid attempt");
            int counter = user.getInvalidAttemptCount();
            if(counter < 2){
                user.setInvalidAttemptCount(++counter);
                userRepository.save(user);
            } else{
                user.setLocked(true);
                user.setInvalidAttemptCount(0);
                userRepository.save(user);
                emailService.sendAccountLockedMail(user);
                logger.debug("CustomAuthenticationManager::authenticate  account locked");
            }
            logger.error("Exception occurred while authenticating");
            throw new BadCredentialsException("Invalid Credentials");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getAuthority()));
        // reset counter on successful login
        user.setInvalidAttemptCount(0);
        userRepository.save(user);
        logger.debug("CustomAuthenticationManager::authenticate credentials authenticated ");
        logger.info("CustomAuthenticationManager::authenticate execution ended.");
        return new UsernamePasswordAuthenticationToken(username, password,authorities);
    }
}
