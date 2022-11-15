package com.ttn.ecommerce;

import com.ttn.ecommerce.entity.Role;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.RoleRepository;
import com.ttn.ecommerce.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapCommandLineRunner implements CommandLineRunner {

    protected final Log logger = LogFactory.getLog(getClass());

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    @Autowired
    public BootstrapCommandLineRunner(UserRepository userRepository,RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        logger.info("BootstrapCommandLineRunner's run method started.");

        if(roleRepository.count()<1){
            Role role1 = new Role();
            role1.setId(1);
            role1.setAuthority("ADMIN");

            Role role2 = new Role();
            role2.setId(2);
            role2.setAuthority("SELLER");

            Role role3 = new Role();
            role3.setId(3);
            role3.setAuthority("CUSTOMER");

            roleRepository.save(role1);
            roleRepository.save(role2);
            roleRepository.save(role3);

        }
        if(userRepository.count()<1) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = new User();
            user.setFirstName("Super");
            user.setLastName("User");
            user.setEmail("superuser@gmail.com");
            user.setPassword(passwordEncoder.encode("supersecret"));
            Role role = roleRepository.findById(1).get();
            user.setRole(role);
            userRepository.save(user);
        }

        logger.info("populated Roles table and created Admin user");

    }
}
