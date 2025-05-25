import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AuthIntegrationTest {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOkWithValidToken() {
        String token = AuthTestHelper.getAuthToken("testuser@test.com", "password123");

        System.out.printf("Generated token: %s", token);
    }

    @Test
    public void shouldReturnUnauthorizedOnInvalidLogin() {
        String loginPayload = """
                {
                    "email": "invalid_user@test.com",
                    "password": "wrongpassword"
                }
                """;

        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }
}
