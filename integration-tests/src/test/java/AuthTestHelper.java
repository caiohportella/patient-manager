import static io.restassured.RestAssured.given;

public class AuthTestHelper {
    public static String getAuthToken(String email, String password) {
        String loginPayload = """
            {
                "email": "%s",
                "password": "%s"
            }
            """
                .formatted(email, password);

        return given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");
    }
}