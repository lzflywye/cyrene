package com.example.user;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/users")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/me")
    @Transactional
    public UserProfile me() {
        String sub = jwt.getSubject();
        UserProfile user = UserProfile.findByUserId(sub);

        if (user == null) {
            user = new UserProfile();
            user.userId = sub;
            user.email = jwt.getClaim("email");
            user.status = UserStatus.PENDING;
            user.persist();
        }

        return user;
    }

    public record UpdateProfileRequest(
            @NotBlank(message = "Name is required") @Size(max = 50, message = "Please enter a name of 50 characters or less") String displayName) {
    }

    @PUT
    @Path("/profile")
    @Transactional
    public UserProfile updateProfile(@Valid UpdateProfileRequest request) {
        UserProfile user = UserProfile.findByUserId(jwt.getSubject());

        if (user == null)
            throw new NotFoundException();

        if (request.displayName() != null)
            user.displayName = request.displayName();

        if (user.status == UserStatus.PENDING)
            user.status = UserStatus.ACTIVE;

        return user;
    }
}
