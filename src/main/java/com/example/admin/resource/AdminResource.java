package com.example.admin.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.user.entity.UserProfile;
import com.example.user.entity.UserStatus;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
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
public class AdminResource {

    @GET
    @Path("/users")
    public Map<String, Object> listUsers(
            @QueryParam("query") String query,
            @QueryParam("status") UserStatus status,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("10") int pageSize) {

        String queryStr = "1=1";
        Map<String, Object> params = new HashMap<>();

        if (query != null && !query.isBlank()) {
            queryStr += " AND (email LIKE :q OR displayName LIKE :q)";
            params.put("q", "%" + query + "%");
        }
        if (status != null) {
            queryStr += " AND status = :s";
            params.put("s", status);
        }

        long total = UserProfile.count(queryStr, params);
        List<UserProfile> users = UserProfile.find(queryStr, Sort.descending("createdAt"), params)
                .page(Page.of(pageIndex, pageSize))
                .list();

        return Map.of(
                "total", total,
                "data", users,
                "page", pageIndex);
    }

    @GET
    @Path("/stats")
    public Map<String, Long> getStats() {
        return Map.of(
                "total", UserProfile.count(),
                "active", UserProfile.count("status", UserStatus.ACTIVE),
                "pending", UserProfile.count("status", UserStatus.PENDING),
                "suspended", UserProfile.count("status", UserStatus.SUSPENDED));
    }

    @PUT
    @Path("/users/{userId}/status")
    public UserProfile updateStatus(@PathParam("userId") String userId, StatusUpdateRequest request) {
        UserProfile user = UserProfile.findByUserId(userId);
        if (user == null) {
            throw new NotFoundException();
        }

        user.status = request.status;
        return user;
    }

    public record StatusUpdateRequest(UserStatus status) {
    }
}
