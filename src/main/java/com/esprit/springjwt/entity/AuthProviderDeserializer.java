package com.esprit.springjwt.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class AuthProviderDeserializer extends JsonDeserializer<AuthProvider> {
    @Override
    public AuthProvider deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        return AuthProvider.fromString(value);
    }
}
