package com.ttn.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthenticationService {

    @Autowired
    TokenStore tokenStore;


    @Autowired
    private AuthenticationManager authenticationManager;

    public Object userSignIn(String username, String password){
        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(username,password));
        System.out.println(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityContext context = SecurityContextHolder.getContext();
        System.out.println(context);
        Authentication reqAuthObject = context.getAuthentication();
        System.out.println(reqAuthObject);
        Object details = reqAuthObject.getDetails();
        System.out.println(details);
        return details;
    }

    public ResponseEntity<String> userSignOut(HttpServletRequest request) {
        try {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.contains("Bearer")) {
                String tokenValue = authorization.replace("Bearer", "").trim();

                OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
                tokenStore.removeAccessToken(accessToken);

                OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
                tokenStore.removeRefreshToken(refreshToken);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid access token");
        }
        return ResponseEntity.ok().body("Access token invalidated successfully");
    }





}
