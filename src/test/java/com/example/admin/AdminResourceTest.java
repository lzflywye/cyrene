package com.example.admin;

import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.entity.Account;
import com.example.entity.AccountStatus;
import com.example.repository.AccountRepository;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
public class AdminResourceTest {

    @Inject
    AccountRepository userProfileRepository;

    @BeforeEach
    @Transactional
    void setup() {
        userProfileRepository.deleteAll();
        Account account = new Account(UUID.randomUUID().toString(), "target@example.com", "test-admin-target",
                AccountStatus.ACTIVE);
        userProfileRepository.persist(account);
    }

    @Test
    @TestSecurity(user = "attacker", roles = "user")
    void testStatsForbiddenForUser() {
        RestAssured.given()
                .when().get("/api/admin/stats")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void testGetStats() {
        RestAssured.given()
                .when().get("/api/admin/stats")
                .then()
                .statusCode(200)
                .body("total", Matchers.greaterThanOrEqualTo(1))
                .body("active", Matchers.greaterThanOrEqualTo(1));
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void testListUsersWithFilter() {
        RestAssured.given()
                .queryParam("query", "target")
                .when().get("/api/admin/users")
                .then()
                .statusCode(200)
                .body("total", Matchers.is(1))
                .body("data[0].email", Matchers.is("target@example.com"));
    }
}
