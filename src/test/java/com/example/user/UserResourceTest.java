package com.example.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.restassured.RestAssured;

@QuarkusTest
public class UserResourceTest {

    @Test
    @TestSecurity(user = "alice", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "sub", value = "alice-id"),
            @Claim(key = "email", value = "alice@example.com")
    })
    void testMeEndpointCreatesUser() {
        RestAssured.given()
                .when().get("/api/users/me")
                .then()
                .statusCode(200)
                .body("userId", Matchers.is("alice-id"))
                .body("email", Matchers.is("alice@example.com"))
                .body("status", Matchers.is("PENDING"));

        UserProfile user = UserProfile.findByUserId("alice-id");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("alice@example.com", user.email);
    }

    @Test
    @TestSecurity(user = "bob", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "sub", value = "bob-id"),
    })
    void testUpdateProfile() {
        RestAssured.given().get("/api/users/me").then().statusCode(200);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"displayName\": \"New Bob\"}")
                .when().put("/api/users/profile")
                .then()
                .statusCode(200)
                .body("displayName", Matchers.is("New Bob"))
                .body("status", Matchers.is("ACTIVE"));
    }
}
