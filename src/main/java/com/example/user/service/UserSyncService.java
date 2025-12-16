package com.example.user.service;

import org.jboss.logging.Logger;

import com.example.user.entity.UserProfile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserSyncService {

    private static final Logger LOG = Logger.getLogger(UserSyncService.class);

    @Inject
    IdentityProvider identityProvider;

    @Transactional
    public void syncEmail(String userId, String newEmail) {
        try {
            LOG.info("Processing sync logic for: " + userId);

            identityProvider.updateEmail(userId, newEmail);

            UserProfile user = UserProfile.findByUserId(userId);
            if (user != null) {
                user.isEmailSynced = true;
                user.persist();
            }

        } catch (Exception e) {
            LOG.error("Failed to sync identity provider", e);
            throw e;
        }
    }
}
