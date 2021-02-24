package functional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Sequences_ {
    private static final int PORT = 5678;
    private static final int DB_PORT = 7017;
    private static final String testFolderPath = "test/resources/sequences/";
    private static Database db;
    private WireMockServer mockServer;

    @Test
    public void given_notAPair_when_postToSequences_then_statusCode400() {
        given().
                multiPart("pair1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(400).
                body("message", equalTo("Cuerpo de la petición no válido"));
    }

    @Test
    public void given_aPairOfValidFiles_when_postToSequences_then_statusCode202_and_sequenceCreated() throws IOException {
        final String token = "123e4567-e89b-12d3-a456-556642440000";
        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"" + token + "\"}")));

        String response =
        given().
                multiPart("pair1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                multiPart("pair2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
        when().
                post("/sequences").
        then().
                statusCode(202).
                extract().
                asString();

        verify(exactly(1), postRequestedFor(urlEqualTo("/trim")));

        ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
        String id = String.valueOf(node.get("id").asText());
        Map<String, Object> sequence = db.get("sequences", id);

        assertThat(sequence.get("strain")).isEqualTo("Klebsiella pneumoniae");
        assertThat(sequence.get("originalFilenames")).isEqualTo(Arrays.asList("Kp1_231120_R1.fastq.gz", "Kp1_231120_R2.fastq.gz"));
        assertThat(sequence.get("genomeToolToken")).isEqualTo(token);
        assertThat(sequence.get("sequenceDate").toString())
                .isEqualTo(dateFormat(date(23, 11, 2020), "yyyy-MM-dd"));
        assertThat(sequence.get("trimRequestDate").toString())
                .startsWith(dateFormat(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm"));
        assertThat(sequence.get("trimmedPair")).isNull();
    }

    @Test
    public void given_aRequestWithoutRequiredFields_when_postToSequencesTrimmed_then_badRequest() {
        given().
                param("status", 5).
                param("message", "Internal error encountered.").
        when().
                post("/sequences/trimmed").
        then().
                statusCode(400);


        given().
                param("status", 2).
                param("token", "123e4567-e89b-12d3-a456-556642440000").
        when().
                post("/sequences/trimmed").
        then().
                statusCode(400);

        given().
                param("status", 2).
                multiPart("pair1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                multiPart("pair2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
        when().
                post("/sequences/trimmed").
        then().
                statusCode(400);

        given().
                param("token", "123e4567-e89b-12d3-a456-556642440000").
                multiPart("pair1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                multiPart("pair2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
        when().
                post("/sequences/trimmed").
        then().
                statusCode(400);
    }

    @Test
    public void given_anErrorMessage_when_postToSequencesTrimmed_then_setTrimmedPairFieldToFalse() throws IOException {
        String token = "123e4567-e89b-12d3-a456-556642440021";
        db.insertFakeSequence(token);

        given().
                param("status", 5).
                param("message", "Internal error encountered.").
                param("token", token).
        when().
                post("/sequences/trimmed").
        then().
                statusCode(200);

        Map<String, Object> sequence = db.get("sequences", "genomeToolToken", token);
        assertThat(sequence.get("trimmedPair")).isEqualTo(false);
        db.delete("sequences", "genomeToolToken", token);
    }

    private Date date(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day);
        return calendar.getTime();
    }

    private String dateFormat(Date date, String format) {
        SimpleDateFormat formattedDate = new SimpleDateFormat(format);
        formattedDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formattedDate.format(date);
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

    @BeforeAll
    static void startCleanDatabase() throws IOException {
        ProcessBuilder process = new ProcessBuilder("test/start_db.sh", String.valueOf(DB_PORT));
        process.start();
        db = new MongoDB(DB_PORT);
    }

    private static boolean theApplicationIsRunning() {
        try {
            RestAssured.get("/alive");
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

    @BeforeEach
    public void startWireMockServer() {
        mockServer = new WireMockServer(options()
                .port(7717));
        mockServer.start();
        configureFor(mockServer.port());
    }

    @AfterEach
    public void stopWireMockServer() {
        mockServer.stop();
    }
}
