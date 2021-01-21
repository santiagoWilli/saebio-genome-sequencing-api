import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.Arrays;

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
    public void given_notAPair_when_postToSequences_then_statusCode400() {
        given().
                multiPart("pair1", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(400).
                body("message", equalTo("La secuencia debe ser una pareja de ficheros."));
    }

    @Test
    public void given_aPairButNotOfTheSameSequency_when_postToSequences_then_statusCode400() {
        given().
                multiPart("pair1", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
                multiPart("pair2", new File("test/sequences/Kp1_231120_R2.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(400).
                body("message", equalTo("Los ficheros no forman una pareja válida."));
    }

    @Test
    public void given_pairFilesAreTheSame_when_postToSequences_then_statusCode400() {
        given().
                multiPart("pair1", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
                multiPart("pair2", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(400).
                body("message", equalTo("Los ficheros no forman una pareja válida."));
    }

    @Test
    public void given_aPairFileWithIncorrectNameConvention_when_postToSequences_then_statusCode400() {
        String[][] wrongFilenames = {
                {"Kpneu1_R1.fastq.gz", "Kpneu1_R2.fastq.gz"},
                {"Kpneu1_R1_050121.fastq.gz", "Kpneu1_191120_R2.fastq.gz"},
                {"Kpneu1_191120_R1.fastq.gz", "R2_Kpneu1_050121.fastq.gz"},
                {"Kpneu1_191120_R1.fastq.gz", "R2_Kpneu1_050121.fastq.gz"},
                {"050121_Kpneu1_R2.fastq.gz", "Kpneu1_191120_R2.fastq.gz"},
        };

        Arrays.stream(wrongFilenames).forEach(pair ->
            given().
                    multiPart("pair1", new File("test/sequences/" + pair[0])).
                    multiPart("pair2", new File("test/sequences/" + pair[1])).
            when().
                    post("/sequences").
            then().
                    statusCode(400).
                    body("message", equalTo("Nombre de archivo inválido."))
        );
    }

    @Test
    public void given_aPairOfValidFiles_when_postToSequences_then_statusCode201() {
        given().
                multiPart("pair1", new File("test/sequences/Kpneu1_191120_R1.fastq.gz")).
                multiPart("pair2", new File("test/sequences/Kpneu1_191120_R2.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(201);
    }
}
