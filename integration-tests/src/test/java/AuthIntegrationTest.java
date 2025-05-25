import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class AuthIntegrationTest {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }
}
