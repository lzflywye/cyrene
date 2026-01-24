package com.example.controller;

import java.util.List;
import java.util.Map;

import com.example.entity.Account;
import com.example.entity.AccountStatus;
import com.example.repository.AccountRepository;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/admin")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminController {

    @Inject
    AccountRepository userProfileRepository;

    @GET
    @Path("/stats")
    public Map<String, Long> getStats() {
        return Map.of(
                "total", userProfileRepository.countAll(),
                "active", userProfileRepository.countByStatus(AccountStatus.ACTIVE),
                "pending", userProfileRepository.countByStatus(AccountStatus.PENDING),
                "suspended", userProfileRepository.countByStatus(AccountStatus.SUSPENDED));
    }

    public record UserPageResponse(
            long total,
            List<Account> data,
            int page) {
    }

    @GET
    @Path("/users")
    public UserPageResponse list(
            @QueryParam("query") String query,
            @QueryParam("status") AccountStatus status,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("10") int pageSize) {

        long total = userProfileRepository.countSearch(query, status);
        List<Account> users = userProfileRepository.findSearch(query, status, pageIndex, pageSize);

        return new UserPageResponse(total, users, pageIndex);
    }

    @PUT
    @Path("/users/{id}/status")
    @Transactional
    public Account setStatus(@PathParam("id") String id, AccountStatus status) {
        Account user = userProfileRepository.findBySub(id);

        if (user == null)
            throw new NotFoundException();

        user.setStatus(status);
        return user;
    }
}
