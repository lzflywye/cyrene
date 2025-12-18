package com.example.admin.resource;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;

@QuarkusTest
public class AdminResourceTest {

    @Test
    @TestSecurity(user = "admin", roles = { "admin" })
    public void testGetStatsAsAdmin() {
        RestAssured.given()
                .when().get("/api/admin/stats")
                .then()
                .statusCode(200)
                .body("total", Matchers.greaterThanOrEqualTo(0));
    }

    @Test
    @TestSecurity(user = "cyrene", roles = { "user" })
    public void testGetStatsAsUser() {
        RestAssured.given()
                .when().get("/api/admin/stats")
                .then()
                .statusCode(403);
    }

    @Test
    public void testGetStatsAnonymous() {
        RestAssured.given()
                .when().get("/api/admin/stats")
                .then()
                .statusCode(401);
    }
}
