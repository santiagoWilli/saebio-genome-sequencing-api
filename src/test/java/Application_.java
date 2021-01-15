import org.bson.Document;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

import java.io.File;

public class Application_ {
    static {
        port = 4567;
    }

    @Test
    public void given_noFiles_when_postToSequencies_then_statusCode400() {
        when().
                post("/sequencies").
        then().
                statusCode(400);
    }

    @Test
    public void given_aPairOfFiles_when_postToSequencies_then_statusCode201() {
        given().
                multiPart("pair1", new File("test/sequencies/Kpneu1_191120_R1.fastq.gz")).
                multiPart("pair2", new File("test/sequencies/Kpneu1_191120_R2.fastq.gz")).
        when().
                post("/sequencies").
        then().
                statusCode(201);
    }
}
