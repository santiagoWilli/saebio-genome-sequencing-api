package functional;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class Sequences_ {
    static final int PORT = 5678;
    static final int DB_PORT = 7017;
    static final String testFolderPath = "test/resources/sequences/";

    @Test
    public void given_notAPair_when_postToSequences_then_statusCode400() {
        given().
                multiPart("pair1", new File(testFolderPath + "Kpneu1_191120_R1.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(400).
                body("message", equalTo("La secuencia debe ser una pareja de ficheros"));
    }

    @BeforeAll
    static void startCleanDatabase() throws IOException {
        ProcessBuilder process = new ProcessBuilder("test/start_db.sh", String.valueOf(DB_PORT));
        process.start();
    }

    @BeforeAll
    static void startApplication() throws IOException, InterruptedException {
        port = PORT;
        ProcessBuilder process = new ProcessBuilder(
                "test/start_application.sh",
                String.valueOf(PORT),
                String.valueOf(DB_PORT));
        process.start();

        int attemptsLeft = 10;
        while (attemptsLeft-- > 0) {
            if (theApplicationIsRunning()) continue;
            System.out.println("Waiting for the application... (attemps left "+attemptsLeft+")");
            Thread.sleep(1000);
        }
    }

    private static boolean theApplicationIsRunning() {
        try {
            get("/alive");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @AfterAll
    static void stopApplication() throws IOException {
        ProcessBuilder process = new ProcessBuilder("test/stop_application.sh");
        process.start();
    }

    @AfterAll
    static void cleanAndStopDatabase() throws IOException {
        ProcessBuilder process = new ProcessBuilder("test/stop_db.sh", String.valueOf(DB_PORT));
        process.start();
    }
}
