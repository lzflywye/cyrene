package com.example.admin;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.user.UserProfile;
import com.example.user.UserStatus;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;

@QuarkusTest
public class AdminResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        UserProfile.deleteAll();
        UserProfile user = new UserProfile();
        user.userId = "test-admin-target";
        user.email = "target@example.com";
        user.status = UserStatus.ACTIVE;
        user.persist();
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
