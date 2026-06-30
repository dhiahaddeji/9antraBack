package com.esprit.springjwt.config;

import com.esprit.springjwt.entity.ERole;
import com.esprit.springjwt.entity.Role;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.RoleRepository;
import com.esprit.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.esprit.springjwt.entity.AuthProvider.local;

@Configuration
public class DataInitializer {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Bean
    @Transactional
    public CommandLineRunner loadData(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role studentRole = new Role();
                studentRole.setName(ERole.ETUDIANT);
                Role adminRole = new Role();
                adminRole.setName(ERole.ADMINISTRATEUR);
                Role formateurRole = new Role();
                formateurRole.setName(ERole.FORMATEUR);
                roleRepository.save(studentRole);
                roleRepository.save(adminRole);
                roleRepository.save(formateurRole);
            }
            if (userRepository.count() == 0) {
                Role adminRole = roleRepository.findByName(ERole.ADMINISTRATEUR).orElseThrow();


                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);


                User admin = new User();
                admin.setUsername("nourelhouda.chawebi@esprit.tn");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFirstName("admin");
                admin.setLastName("admin");
                admin.setNumeroTel("96360730");
                admin.setProvider(local);
                admin.setEnabled(1);
                admin.setEmail_verified_at(new Date());
                admin.setRoles(adminRoles);
                admin.setCountry("tunisia");
                admin.setAbout("just an admin");
                userRepository.save(admin);


            }
        };
    }}