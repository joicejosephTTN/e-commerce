package com.ttn.ecommerce.service;

import com.ttn.ecommerce.exception.InvalidTokenException;
import com.ttn.ecommerce.security.CustomAuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;


@Service
public class AuthenticationService {

    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    MessageSource messageSource;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    private CustomAuthenticationManager authenticationManager;

    public Object userSignIn(String username, String password){
        logger.info("AuthenticationService::userSignIn execution started.");

        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(username,password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication reqAuthObject = context.getAuthentication();
        Object details = reqAuthObject.getDetails();

        logger.info("AuthenticationService::userSignIn execution ended.");

        return details;
    }

    public ResponseEntity<String> userSignOut(HttpServletRequest request) {
        logger.info("AuthenticationService::userSignOut execution started.");

        try {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.contains("Bearer")) {
                String tokenValue = authorization.replace("Bearer", "").trim();

                logger.debug("AuthenticationService::userSignIn revoking tokens.");

                OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
                tokenStore.removeAccessToken(accessToken);
                OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
                tokenStore.removeRefreshToken(refreshToken);
            }
        } catch (Exception e) {
            logger.error("An exception occurred while signing out");
            throw new InvalidTokenException(messageSource.getMessage("api.error.invalidAccessToken",null, Locale.ENGLISH));
        }
        logger.info("AuthenticationService::userSignOut execution ended.");
        return ResponseEntity.ok().body(messageSource.getMessage("api.response.tokenInvalidated",null, Locale.ENGLISH));
    }





}
