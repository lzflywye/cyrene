package com.example.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.restassured.RestAssured;
import jakarta.inject.Inject;

@QuarkusTest
public class UserResourceTest {

    @Inject
    AccountRepository accountRepository;

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
                .body("sub", Matchers.is("alice-id"))
                .body("email", Matchers.is("alice@example.com"))
                .body("status", Matchers.is("PENDING"));

        Account account = accountRepository.findBySub("alice-id");
        Assertions.assertNotNull(account);
        Assertions.assertEquals("alice@example.com", account.getEmail());
    }

    @Test
    @TestSecurity(user = "bob", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "sub", value = "bob-id"),
            @Claim(key = "email", value = "bob@example.com")
    })
    void testUpdateProfile() {
        RestAssured.given().get("/api/users/me").then().statusCode(200);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"fullName\": \"New Bob\"}")
                .when().put("/api/users/profile")
                .then()
                .statusCode(200)
                .body("fullName", Matchers.is("New Bob"))
                .body("status", Matchers.is("ACTIVE"));
    }
}
