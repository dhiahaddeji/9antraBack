package com.esprit.springjwt.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AuthProviderConverter implements AttributeConverter<AuthProvider, String> {
    
    @Override
    public String convertToDatabaseColumn(AuthProvider attribute) {
        if (attribute == null) {
            return "local";
        }
        return attribute.name();
    }

    @Override
    public AuthProvider convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return AuthProvider.local;
        }
        try {
            return AuthProvider.valueOf(dbData.toLowerCase());
        } catch (IllegalArgumentException e) {
            // Handle invalid values by defaulting to 'local'
            return AuthProvider.local;
        }
    }
}
