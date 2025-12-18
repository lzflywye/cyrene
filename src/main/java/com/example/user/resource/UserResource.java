package com.example.user.resource;

import java.time.Instant;

import org.jboss.resteasy.reactive.NoCache;

import com.example.security.CurrentUser;
import com.example.user.entity.UserProfile;
import com.example.user.entity.UserStatus;
import com.example.user.resource.dto.UpdateProfileRequest;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/api/users")
public class UserResource {

    @Inject
    CurrentUser currentUser;

    @GET
    @Path("/me")
    @NoCache
    public UserProfile me() {
        return currentUser.getOrCreateProfile();
    }

    @PUT
    @Path("/profile")
    @Transactional
    public UserProfile updateProfile(@Valid UpdateProfileRequest request) {
        UserProfile user = currentUser.getOrCreateProfile();

        user.displayName = request.displayName();

        if (user.status == UserStatus.PENDING) {
            user.status = UserStatus.ACTIVE;
        }

        user.updatedAt = Instant.now();
        user.persist();

        return user;
    }
}
