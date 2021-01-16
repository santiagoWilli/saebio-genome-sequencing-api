import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

import java.io.File;

public class Application_ {

    static {
        port = 4567;
    }

    @Test
    public void given_noMultipartBody_when_postToSequences_then_statusCode400() {
        when().
                post("/sequences").
        then().
                statusCode(400);
    }

    @Test
    public void given_aPairOfFiles_when_postToSequences_then_statusCode201() {
        given().
                multiPart("pair1", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
                multiPart("pair2", new File("test/sequences/Kpneu1_191120_R2.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(201);
    }

    @Test
    public void given_notAPair_when_postToSequences_then_statusCode400() {
        given().
                multiPart("pair1", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(400);
    }

    @Test
    public void given_aPairButNotOfTheSameSequency_when_postToSequences_then_statusCode400() {
        given().
                multiPart("pair1", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
                multiPart("pair2", new File("test/sequences/Kp1_231120_R2.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(400);
    }

    @Test
    public void given_pairFilesAreTheSame_when_postToSequences_then_statusCode400() {
        given().
                multiPart("pair1", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
                multiPart("pair2", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(400);
    }
}
