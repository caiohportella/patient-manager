import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientIntegrationTest {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientsWithValidToken() {
        String token = AuthTestHelper.getAuthToken();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", Matchers.notNullValue());
    }

    @Test
    public void shouldReturn429AfterLimitExceeded() throws InterruptedException {
        String token = AuthTestHelper.getAuthToken();
        int totalRequests = 10;
        int tooManyRequests = 0;

        for (int i = 1; i <= totalRequests; i++) {
            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + token)
                    .get("/api/patients");

            System.out.printf("Request %d -> Status: %d%n", i, response.getStatusCode());

            if(response.statusCode() == 429) {
                tooManyRequests++;
            }

            Thread.sleep(100);
        }

        assertTrue(tooManyRequests >= 1,
                "Expected at least 1 request to be limited (429)");
    }
}