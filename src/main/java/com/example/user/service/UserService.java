package com.example.user.service;

import java.time.Instant;

import com.example.user.entity.UserProfile;
import com.example.user.event.EventPublisher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {

    @Inject
    EventPublisher eventPublisher;

    @Transactional
    public void updateEmail(String userId, String newEmail) {
        UserProfile user = UserProfile.findByUserId(userId);
        if (user == null)
            throw new IllegalArgumentException("User not found");

        user.email = newEmail;
        user.isEmailSynced = false;
        user.updatedAt = Instant.now();
        user.persist();

        eventPublisher.publishEmailUpdate(userId, newEmail);
    }
}
