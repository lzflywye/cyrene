package com.example.security;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.example.user.entity.UserProfile;
import com.example.user.entity.UserStatus;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CurrentUser {

    @Inject
    JsonWebToken token;

    public String getId() {
        return token.getSubject();
    }

    public String getEmail() {
        return token.getClaim("email");
    }

    @Transactional
    public UserProfile getOrCreateProfile() {
        String userId = getId();
        UserProfile profile = UserProfile.findByUserId(userId);

        if (profile == null) {
            profile = new UserProfile();
            profile.userId = userId;
            profile.email = getEmail();
            profile.status = UserStatus.PENDING;
            profile.isEmailSynced = true;
            profile.persist();
        }

        return profile;
    }
}
