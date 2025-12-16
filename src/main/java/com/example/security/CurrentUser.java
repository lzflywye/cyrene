package com.example.security;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CurrentUser {

    @Inject
    JsonWebToken token;

    public String getId() {
        return token.getSubject();
    }

    public String getTokenEmail() {
        return token.getClaim("email");
    }
}
