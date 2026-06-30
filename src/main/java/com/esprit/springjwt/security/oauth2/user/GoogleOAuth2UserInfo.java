package com.esprit.springjwt.security.oauth2.user;

import java.util.Map;
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.getOrDefault("sub", "");
    }

    @Override
    public String getName() {
        return (String) attributes.getOrDefault("name", "");
    }

    @Override
    public String getEmail() {
        return (String) attributes.getOrDefault("email", "");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.getOrDefault("picture", "");
    }

    @Override
    public String getFirstName() {
        String name = getName();
        String[] nameParts = name != null ? name.split(" ") : new String[0];
        return nameParts.length > 0 ? nameParts[0] : "";
    }

    @Override
    public String getLastName() {
        String name = getName();
        String[] nameParts = name != null ? name.split(" ") : new String[0];
        return nameParts.length > 1 ? nameParts[1] : "";
    }

    @Override
    public String getPhoneNumber() {
        return "";
    }

    @Override
    public String getCountry() {
        return "";
    }
}