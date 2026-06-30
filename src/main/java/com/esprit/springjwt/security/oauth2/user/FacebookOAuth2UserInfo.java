package com.esprit.springjwt.security.oauth2.user;

import java.util.Map;
import java.util.logging.Logger;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {
    private static final Logger logger = Logger.getLogger(FacebookOAuth2UserInfo.class.getName());

    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        logAttributes();
    }

    private void logAttributes() {
        logger.info("Facebook User Attributes: " + attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.getOrDefault("id", "");
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
        if (attributes.containsKey("picture")) {
            Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
            if (pictureObj != null && pictureObj.containsKey("data")) {
                Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
                if (dataObj != null && dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }
        return "";
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