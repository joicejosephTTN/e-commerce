package com.ttn.ecommerce.config;

import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.UserRepository;
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

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findUserByEmail(username);

        if(user==null){
            throw new UsernameNotFoundException("Invalid credentials");
        }
        if(user.isLocked()){
            throw new BadCredentialsException("Account is locked"); // change the exception
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            int counter = user.getInvalidAttemptCount();
            if(counter < 2){
                user.setInvalidAttemptCount(++counter);
                userRepository.save(user);
            } else{
                user.setLocked(true);
                user.setInvalidAttemptCount(0);
                userRepository.save(user);
            }
            throw new BadCredentialsException("Invalid Credentials");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getAuthority()));

        user.setInvalidAttemptCount(0); // reset counter on successful login
        userRepository.save(user);
        return new UsernamePasswordAuthenticationToken(username, password,authorities);
    }
}
