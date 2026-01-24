package com.example.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.example.entity.Account;
import com.example.entity.AccountStatus;
import com.example.repository.AccountRepository;

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
public class UserController {

    @Inject
    JsonWebToken jwt;

    @Inject
    AccountRepository accountRepository;

    @GET
    @Path("/me")
    @Transactional
    public Account me() {
        String sub = jwt.getSubject();
        Account account = accountRepository.findBySub(sub);

        if (account == null) {
            account = new Account(sub, jwt.getClaim("email"), null, AccountStatus.PENDING);
            accountRepository.persist(account);
        }

        return account;
    }

    public record UpdateProfileRequest(
            @NotBlank(message = "Name is required") @Size(max = 50, message = "Please enter a name of 50 characters or less") String fullName) {
    }

    @PUT
    @Path("/profile")
    @Transactional
    public Account updateProfile(@Valid UpdateProfileRequest request) {
        Account account = accountRepository.findBySub(jwt.getSubject());

        if (account == null)
            throw new NotFoundException();

        if (request.fullName() != null)
            account.setFullName(request.fullName());

        if (account.getStatus() == AccountStatus.PENDING)
            account.setStatus(AccountStatus.ACTIVE);

        accountRepository.persist(account);

        return account;
    }
}
