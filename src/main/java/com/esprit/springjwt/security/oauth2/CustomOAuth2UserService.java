package com.esprit.springjwt.security.oauth2;

import com.esprit.springjwt.entity.AuthProvider;
import com.esprit.springjwt.entity.ERole;
import com.esprit.springjwt.entity.Role;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.exception.OAuth2AuthenticationProcessingException;
import com.esprit.springjwt.repository.RoleRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.security.oauth2.user.OAuth2UserInfo;
import com.esprit.springjwt.security.oauth2.user.OAuth2UserInfoFactory;
import com.esprit.springjwt.security.services.UserPrincipal;
import com.esprit.springjwt.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private userService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) throws IOException {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );

        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        // Look for an existing user by email
        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        if (user != null) {
            // If the user exists, update their provider info if necessary
            if (!user.getProvider().name().equalsIgnoreCase(oAuth2UserRequest.getClientRegistration().getRegistrationId())) {
                user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toLowerCase()));
                user.setProviderId(oAuth2UserInfo.getId());
            }
            // Update the existing user
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            // Register a new user
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        // Return the UserPrincipal with the updated or new user
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) throws IOException {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toLowerCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setFirstName(oAuth2UserInfo.getFirstName());
        user.setLastName(oAuth2UserInfo.getLastName());
        user.setUsername(oAuth2UserInfo.getEmail());
        String imageName = userService.saveImageFromUrl(oAuth2UserInfo.getImageUrl());
        user.setImage(imageName);

        user.setCountry("Tunisia");
        Date d = new Date();
        user.setEmail_verified_at(d);
        Role studentRole = roleRepository.findByName(ERole.ETUDIANT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) throws IOException {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName()); // Utilise le prénom
        existingUser.setLastName(oAuth2UserInfo.getLastName());   // Utilise le nom de famille

        // Met à jour l'URL de l'image
        String imageName = userService.saveImageFromUrl(oAuth2UserInfo.getImageUrl());
        existingUser.setImage(imageName);

        return userRepository.save(existingUser);
    }
}