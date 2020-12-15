import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

public class Application_ {
    static {
        port = 4567;
    }

    @Test
    public void createASequencyShouldReturn201() {
        given().
                post("/sequencies").
        then().
                statusCode(201);
    }
}
