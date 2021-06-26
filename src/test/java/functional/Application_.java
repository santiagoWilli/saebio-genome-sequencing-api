package functional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Application_ {
    private static final int PORT = 5678;
    private static final int DB_PORT = 7017;
    private static final int WIREMOCK_PORT = 7717;
    private static final String testFolderPath = "test/resources/sequences/";
    private static RequestSpecification requestSpec;
    private static Database db;
    private WireMockServer mockServer;

    @Test
    public void given_notAPair_when_postToSequences_then_statusCode400() {
        given().
                spec(requestSpec).
                multiPart("file1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
        when().
                post("/api/sequences").
        then().
                statusCode(400).
                body("message", equalTo("Cuerpo de la petición no válido"));
    }

    @Test
    public void given_aPairOfValidFiles_when_postToSequences_then_statusCode202_and_sequenceCreated() throws IOException {
        db.insertFakeStrain("kp");
        Map<String, Object> strain = db.get("strains", "keys", "kp");

        final String token = token();
        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"" + token + "\"}")));

        String response =
            given().
                    spec(requestSpec).
                    multiPart("file1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                    multiPart("file2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
            when().
                    post("/api/sequences").
            then().
                    statusCode(202).
                    extract().
                    asString();

        verify(exactly(1), postRequestedFor(urlEqualTo("/trim")));

        ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
        String id = String.valueOf(node.get("id").asText());
        Map<String, Object> sequence = db.get("sequences", id);

        assertThat(sequence.get("strain")).isEqualTo(strain.get("_id"));
        assertThat(sequence.get("code")).isEqualTo("1");
        assertThat(sequence.get("originalFilenames")).isEqualTo(Arrays.asList("Kp1_231120_R1.fastq.gz", "Kp1_231120_R2.fastq.gz"));
        assertThat(sequence.get("genomeToolToken")).isEqualTo(token);
        assertThat(sequence.get("sequenceDate").toString())
                .isEqualTo(dateFormat(date(23, 11, 2020), "yyyy-MM-dd"));
        assertThat(sequence.get("trimRequestDate").toString())
                .startsWith(dateFormat(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm"));
        assertThat(sequence.get("trimmedPair")).isNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void given_aPairOfValidTrimmedFiles_when_postToSequences_then_statusCode200_and_sequenceCreatedWithItsTrimmedFiles() throws IOException {
        db.insertFakeStrain("kp");
        Map<String, Object> strain = db.get("strains", "keys", "kp");

        stubFor(post(urlEqualTo("/trim")).willReturn(aResponse()));

        String response =
                given().
                        spec(requestSpec).
                        multiPart("file1", new File(testFolderPath + "Kp1_231120_R1_trimmed.fastq.gz")).
                        multiPart("file2", new File(testFolderPath + "Kp1_231120_R2_trimmed.fastq.gz")).
                when().
                        post("/api/sequences").
                then().
                        statusCode(200).
                        extract().
                        asString();

        verify(exactly(0), postRequestedFor(urlEqualTo("/trim")));

        ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
        String id = String.valueOf(node.get("id").asText());
        Map<String, Object> sequence = db.get("sequences", id);

        assertThat(sequence.get("strain")).isEqualTo(strain.get("_id"));
        assertThat(sequence.get("code")).isEqualTo("1");
        assertThat(sequence.get("originalFilenames")).isEqualTo(Arrays.asList("Kp1_231120_R1_trimmed.fastq.gz", "Kp1_231120_R2_trimmed.fastq.gz"));
        assertThat(sequence.get("genomeToolToken")).isNull();
        assertThat(sequence.get("sequenceDate").toString())
                .isEqualTo(dateFormat(date(23, 11, 2020), "yyyy-MM-dd"));
        assertThat(sequence.get("trimRequestDate")).isNull();
        assertThat(sequence.get("trimmedPair")).isOfAnyClassIn(ArrayList.class);

        ArrayList<Map<String, String>> trimmedPair = (ArrayList<Map<String, String>>) sequence.get("trimmedPair");
        assertThat(trimmedPair.size()).isEqualTo(2);
        for (Map<String, String> trimmedFile : trimmedPair) assertThat(trimmedFile.containsKey("$oid"));
    }

    @Test
    public void given_aPairOfValidFiles_and_sequenceAlreadyExists_when_postToSequences_then_statusCode409() throws IOException {
        String id = db.insertFakeStrain("kp");
        db.insertFakeRepeatedSequence(id, "1", "2020-11-23");

        stubFor(post(urlEqualTo("/trim")).willReturn(aResponse()));

        given().
                spec(requestSpec).
                multiPart("file1", new File(testFolderPath + "Kp1_231120_R1_trimmed.fastq.gz")).
                multiPart("file2", new File(testFolderPath + "Kp1_231120_R2_trimmed.fastq.gz")).
        when().
                post("/api/sequences").
        then().
                statusCode(409);

        verify(exactly(0), postRequestedFor(urlEqualTo("/trim")));
    }

    @Test
    public void given_aPairOfFilesWhichStrainKeysDoNotExist_when_postToSequences_then_httpBadRequest() {
        stubFor(post(urlEqualTo("/trim")));

        given().
                spec(requestSpec).
                multiPart("file1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                multiPart("file2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
        when().
                post("/api/sequences").
        then().
                statusCode(400);

        verify(exactly(0), postRequestedFor(urlEqualTo("/trim")));
    }

    @Test
    public void given_aRequestWithoutRequiredFields_when_postToSequencesTrimmed_then_badRequest() {
        given().
                spec(requestSpec).
                multiPart("status", 5).
                multiPart("message", "Internal error encountered.").
        when().
                post("/api/sequences/trimmed").
        then().
                statusCode(400);

        given().
                spec(requestSpec).
                multiPart("status", 2).
                multiPart("token", "123e4567-e89b-12d3-a456-556642440000").
        when().
                post("/api/sequences/trimmed").
        then().
                statusCode(400);

        given().
                spec(requestSpec).
                multiPart("status", 2).
                multiPart("file1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                multiPart("file2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
        when().
                post("/api/sequences/trimmed").
        then().
                statusCode(400);

        given().
                spec(requestSpec).
                multiPart("token", "123e4567-e89b-12d3-a456-556642440000").
                multiPart("file1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                multiPart("file2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
        when().
                post("/api/sequences/trimmed").
        then().
                statusCode(400);
    }

    @Test
    public void given_anErrorMessage_when_postToSequencesTrimmed_then_setTrimmedPairFieldToFalse() throws IOException {
        final String token = token();
        db.insertFakeSequence(token);

        given().
                spec(requestSpec).
                multiPart("status", 5).
                multiPart("message", "Internal error encountered.").
                multiPart("token", token).
        when().
                post("/api/sequences/trimmed").
        then().
                statusCode(200);

        Map<String, Object> sequence = db.get("sequences", "genomeToolToken", token);
        assertThat(sequence.get("trimmedPair")).isEqualTo(false);
    }

    @Test
    public void given_aSuccessfulStatus_and_tokenThatDoesNotExist_when_postToSequencesTrimmed_then_notFound() {
        final String token = token();

        given().
                spec(requestSpec).
                multiPart("status", 2).
                multiPart("token", token).
                multiPart("file1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                multiPart("file2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
        when().
                post("/api/sequences/trimmed").
        then().
                statusCode(404);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void given_aSuccessfulStatus_when_postToSequencesTrimmed_then_uploadTrimmedFiles_and_associateThemToItsSequence() throws IOException {
        final String token = token();
        db.insertFakeSequence(token);

        given().
                spec(requestSpec).
                multiPart("status", 2).
                multiPart("token", token).
                multiPart("file1", new File(testFolderPath + "Kp1_231120_R1.fastq.gz")).
                multiPart("file2", new File(testFolderPath + "Kp1_231120_R2.fastq.gz")).
        when().
                post("/api/sequences/trimmed").
        then().
                statusCode(200);

        Map<String, Object> sequence = db.get("sequences", "genomeToolToken", token);
        assertThat(sequence.get("trimmedPair")).isNotNull();
        assertThat(sequence.get("trimmedPair")).isNotEqualTo(false);
        assertThat(sequence.get("trimmedPair")).isOfAnyClassIn(ArrayList.class);

        ArrayList<Map<String, String>> trimmedPair = (ArrayList<Map<String, String>>) sequence.get("trimmedPair");
        assertThat(trimmedPair.size()).isEqualTo(2);
        for (Map<String, String> trimmedFile : trimmedPair) assertThat(trimmedFile.containsKey("$oid"));
    }

    @Test
    public void when_getToSequences_then_returnAJsonOfAllSequences() throws IOException {
        int amount = 5;
        insertFakeSequences(amount);

        String response =
            given().
                    spec(requestSpec).
            when().
                    get("/api/sequences").
            then().
                    statusCode(200).
                    extract().asString();

        List<Object> sequences = Arrays.asList(new ObjectMapper().readValue(response, Object[].class));
        assertThat(sequences.size()).isEqualTo(amount);
    }

    @Test
    public void when_getToSequencesSpecifyingAFilter_then_returnAJsonOfAllSequencesThatPassTheFilter() throws IOException {
        String anyStrainId = db.insertFakeStrain("no", "not this one");
        String wantedStrainId = db.insertFakeStrain("yes", "this one!");
        int amount = 5;
        insertFakeSequences(amount, wantedStrainId);
        insertFakeSequences(10, anyStrainId);

        String response =
            given().
                    spec(requestSpec).
                    queryParam("strain", wantedStrainId).
            when().
                    get("/api/sequences").
            then().
                    statusCode(200).
                    extract().asString();

        List<Object> sequences = Arrays.asList(new ObjectMapper().readValue(response, Object[].class));
        assertThat(sequences.size()).isEqualTo(amount);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void when_getToSequencesId_then_returnSequenceWithGivenIdAsJson() throws IOException {
        final String token = token();
        db.insertFakeSequence(token, db.insertFakeStrain("kp", "test"));
        Map<String, Object> sequence = db.get("sequences", "genomeToolToken", token);
        String id = ((Map<String, String>) sequence.get("_id")).get("$oid");

        String response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/sequences/" + id).
                then().
                        statusCode(200).
                        extract().asString();

        Map<String, Object> sequenceJson = new ObjectMapper().readValue(response, new TypeReference<HashMap<String,Object>>(){});
        assertThat(((Map<String, String>) sequenceJson.get("_id")).get("$oid"))
                .isEqualTo(id);
        assertThat(sequenceJson.get("genomeToolToken")).isEqualTo(token);
        Map<String, Object> strain = (Map<String, Object>) sequenceJson.get("strain");
        assertThat(strain.get("_id")).isNotNull();
        assertThat((String) strain.get("name")).isEqualTo("test");
    }

    @Test
    public void when_getToSequencesId_and_notFound_then_returnHttp404() {
        given().
                spec(requestSpec).
        when().
                get("/api/sequences/6075d61d1a62381d13c70a6e").
        then().
                statusCode(404);

        given().
                spec(requestSpec).
        when().
                get("/api/sequences/6075d61d1a62381d13c70").
        then().
                statusCode(404);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void when_getToSequencesIdTrimmed_then_returnTrimmedPairAsZip() throws IOException {
        final String token = token();
        Collection<File> files = new ArrayList<>();
        files.add(new File(testFolderPath + "ngs/Kp4_R1_001_trimmed.fq.gz"));
        files.add(new File(testFolderPath + "ngs/Kp4_R2_001_trimmed.fq.gz"));
        db.insertFakeSequenceWithTrimmedFiles(token, files, db.insertFakeStrain("kp", "test"));
        Map<String, Object> sequence = db.get("sequences", "genomeToolToken", token);
        String id = ((Map<String, String>) sequence.get("_id")).get("$oid");

        byte[] response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/sequences/" + id + "/trimmed").
                then().
                        statusCode(200).
                        contentType("application/zip").
                        header("Content-Disposition", "attachment; filename=file.zip").
                        extract().asByteArray();

        File file = new File("temp/trimmed_test.zip");
        FileUtils.writeByteArrayToFile(file, response);
        ZipFile zip = new ZipFile(file);
        assertThat(zip.size()).isEqualTo(2);

        final Enumeration<? extends ZipEntry> entries = zip.entries();
        int i = 0;
        String[] entryName = new String[2];
        while (entries.hasMoreElements()) {
            String name = entries.nextElement().getName();
            entryName[i++] = name;
            assertThat(name).endsWith("trimmed.fq.gz");
        }
        if (entryName[0].contains("R1")) assertThat(entryName[1]).contains("R2");
        else assertThat(entryName[1]).contains("R1");

        file.delete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void given_aValidFile_when_postToReferences_then_statusCode200_and_referenceUploaded() throws IOException {
        db.insertFakeStrain("kpneu");
        Map<String, Object> strain = db.get("strains", "keys", "kpneu");

        String response =
                given().
                        spec(requestSpec).
                        multiPart("file", new File(testFolderPath + "Kpneu231120_referencia.fa")).
                when().
                        post("/api/references").
                then().
                        statusCode(200).
                        extract().
                        asString();

        ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
        String id = String.valueOf(node.get("id").asText());
        Map<String, Object> reference = db.get("references", id);

        assertThat(reference.get("strain")).isEqualTo(strain.get("_id"));
        assertThat(reference.get("code")).isEqualTo("231120");
        assertThat(reference.get("file")).isNotNull();
        assertThat(reference.get("file")).isOfAnyClassIn(LinkedHashMap.class);
        LinkedHashMap<String, String> file = (LinkedHashMap<String, String>) reference.get("file");
        assertThat(file).containsKey("$oid");
        assertThat(db.referenceExists(file.get("$oid"))).isTrue();
    }

    @Test
    public void given_aValidFile_and_referenceAlreadyExists_when_postToReferences_then_statusCode409() throws IOException {
        String id = db.insertFakeStrain("kpneu");
        db.insertFakeRepeatedReference(id, "231120");

        given().
                spec(requestSpec).
                multiPart("file", new File(testFolderPath + "Kpneu231120_referencia.fa")).
        when().
                post("/api/references").
        then().
                statusCode(409);
    }

    @Test
    public void given_aFileWhichStrainKeyDoesNotExist_when_postToReferences_then_httpBadRequest() {
        given().
                spec(requestSpec).
                multiPart("file", new File(testFolderPath + "Kpneu231120_referencia.fa")).
        when().
                post("/api/references").
        then().
                statusCode(400);
    }

    @Test
    public void when_getToReferences_then_returnAJsonOfAllReferences() throws IOException {
        int amount = 5;
        insertFakeReferences(amount);

        String response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/references").
                then().
                        statusCode(200).
                        extract().asString();

        List<Object> references = Arrays.asList(new ObjectMapper().readValue(response, Object[].class));
        assertThat(references.size()).isEqualTo(amount);
    }

    @Test
    public void when_getToReferencesSpecifyingAFilter_then_returnAJsonOfAllReferencesThatPassTheFilter() throws IOException {
        String anyStrainId = db.insertFakeStrain("no", "not this one");
        String wantedStrainId = db.insertFakeStrain("yes", "this one!");
        int amount = 5;
        insertFakeReferences(amount, wantedStrainId);
        insertFakeReferences(10, anyStrainId);

        String response =
                given().
                        spec(requestSpec).
                        queryParam("strain", wantedStrainId).
                when().
                        get("/api/references").
                then().
                        statusCode(200).
                        extract().asString();

        List<Object> references = Arrays.asList(new ObjectMapper().readValue(response, Object[].class));
        assertThat(references.size()).isEqualTo(amount);
    }

    @Test
    public void when_getToReferencesId_then_returnReferenceFile() throws IOException {
        File file = new File(testFolderPath + "Kpneu231120_referencia.fa");
        String id = db.insertFakeReferenceWithFile(file);

        byte[] response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/references/" + id).
                then().
                        statusCode(200).
                        contentType("text/x-fasta").
                        header("Content-Disposition", "attachment; filename=file.fa").
                        extract().asByteArray();

        InputStream responseStream = new ByteArrayInputStream(response);
        assertThat(IOUtils.contentEquals(responseStream, new FileInputStream(file))).isTrue();
    }

    @Test
    public void when_getToStrains_then_returnStrainsAsJson() throws IOException {
        for (int i = 0; i < 5; i++) db.insertFakeStrain("key" + i, "name" + i);

        String response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/strains").
                then().
                        statusCode(200).
                        contentType("application/json").
                        extract().asString();

        List<Object> strains = Arrays.asList(new ObjectMapper().readValue(response, Object[].class));
        assertThat(strains.size()).isEqualTo(5);
    }

    @Test
    public void given_aKeyAndAName_and_keyDoesNotExist_when_postToStrains_then_strainIsCreatedWithKeyInLowerCase_and_returnHttpOk() throws IOException {
        String key = "newKey";

        given().
                spec(requestSpec).
                param("key", key).
                param("name", "anyName").
        when().
                post("/api/strains").
        then().
                statusCode(200);

        assertThat(db.strainExists(key.toLowerCase())).isTrue();
        Map<String, Object> strain = db.get("strains", "keys", key.toLowerCase());
        assertThat(strain.get("keys").getClass().getInterfaces()).contains(List.class);
    }

    @Test
    public void given_aKeyAndAName_and_nameDoesAlreadyExist_when_postToStrains_then_returnHttpBadRequest() {
        db.insertFakeStrain("kp", "klebsi");

        String response =
                given().
                        spec(requestSpec).
                        param("key", "kneu").
                        param("name", "klebsi").
                when().
                        post("/api/strains").
                then().
                        statusCode(409).
                        extract().asString();

        assertThat(response).contains("name");
        assertThat(response).doesNotContain("key");
    }

    @Test
    public void given_aKeyAndAName_and_keyDoesAlreadyExist_when_postToStrains_then_returnHttpBadRequest() {
        db.insertFakeStrain("kneu", "klebsiella pneumoniae");

        String response =
                given().
                        spec(requestSpec).
                        param("key", "kneu").
                        param("key", "kaer").
                        param("name", "klebsiella aerogenes").
                when().
                        post("/api/strains").
                then().
                        statusCode(409).
                        extract().asString();

        assertThat(response).contains("key");
        assertThat(response).doesNotContain("name");
    }

    @Test
    public void given_anIdThatExists_and_noDocumentPointsToIt_when_deleteToStrainsId_then_deleteStrain_and_returnHttpOk() {
        String key = "key";
        String id = db.insertFakeStrain(key);
        assertThat(db.strainExists(key)).isTrue();

        given().
                spec(requestSpec).
        when().
                delete("/api/strains/" + id).
        then().
                statusCode(200);

        assertThat(db.strainExists(key)).isFalse();
    }

    @Test
    public void given_anIdThatExists_and_oneOrMoreDocumentsPointToIt_when_deleteToStrainsId_then_strainNotDeleted_and_returnHttpConflict() {
        String key = "key";
        String id = db.insertFakeStrain(key);
        db.insertFakeSequence(token(), id);
        assertThat(db.strainExists(key)).isTrue();

        given().
                spec(requestSpec).
        when().
                delete("/api/strains/" + id).
        then().
                statusCode(409);

        assertThat(db.strainExists(key)).isTrue();
    }

    @Test
    public void given_anIdThatDoesNotExist_when_deleteToStrainsId_then_returnHttpNotFound() {
        given().
                spec(requestSpec).
        when().
                delete("/api/strains/1234").
        then().
                statusCode(404);
    }

    @Test
    public void given_keys_and_anyKeyAlreadyExists_when_patchToStrainsId_then_returnHttpConflict_and_strainKeysAreNotModified() throws IOException {
        db.insertFakeStrain("no", "test");
        String id = db.insertFakeStrain("kp");

        given().
                spec(requestSpec).
                param("key", "no").
                param("key", "kneu").
        when().
                patch("/api/strains/" + id).
        then().
                statusCode(409);

        Map<String, Object> strain = db.get("strains", id);
        assertThat((List<String>) strain.get("keys")).containsExactly("kp");
    }

    @Test
    public void given_keys_and_keysDoNotExistInOtherStrains_when_patchToStrainsId_then_keysAreAddedToStrainKeys_and_returnHttpOk() throws IOException {
        String id = db.insertFakeStrain("kp");

        given().
                spec(requestSpec).
                param("key", "kp").
                param("key", "kpn").
                param("key", "kneu").
        when().
                patch("/api/strains/" + id).
        then().
                statusCode(200);

        Map<String, Object> strain = db.get("strains", id);
        assertThat((List<String>) strain.get("keys")).containsExactlyInAnyOrder("kp", "kpn", "kneu");
    }

    @Test
    public void given_idDoesNotExist_when_patchToStrainsId_then_returnHttpNotFound() {
        given().
                spec(requestSpec).
                param("key", "kneu").
        when().
                patch("/api/strains/6075d61d1a62381d13c70a6e").
        then().
                statusCode(404);
    }

    @Test
    public void given_noSequences_when_postToReports_then_returnHttpBadRequest() {
        given().
                spec(requestSpec).
                param("reference", "1").
        when().
                post("/api/reports").
        then().
                statusCode(400);
    }

    @Test
    public void given_noReference_or_multipleReferences_when_postToReports_then_returnHttpBadRequest() {
        given().
                spec(requestSpec).
                param("sequences", "1", "2").
        when().
                post("/api/reports").
        then().
                statusCode(400);

        given().
                spec(requestSpec).
                param("sequences", "1", "2").
                param("reference", "1", "2").
        when().
                post("/api/reports").
        then().
                statusCode(400);
    }

    @Test
    public void given_aReference_and_fourOrMoreSequences_and_theyDoNotShareTheSameStrain_when_postToReports_then_httpConflict() throws IOException {
        final String token = token();
        final String strain1Id = db.insertFakeStrain("kp", "test");
        final String strain2Id = db.insertFakeStrain("zo", "zoo");
        Collection<File> files = new ArrayList<>();
        files.add(new File(testFolderPath + "ngs/Kp4_R1_001_trimmed.fq.gz"));
        files.add(new File(testFolderPath + "ngs/Kp4_R2_001_trimmed.fq.gz"));
        File referenceFile = new File(testFolderPath + "Kpneu231120_referencia.fa");
        String[] sequenceIds = new String[4];
        for (int i = 0; i < 2; i++) sequenceIds[i] = db.insertFakeSequenceWithTrimmedFiles(token, files, strain1Id);
        for (int i = 2; i < 4; i++) sequenceIds[i] = db.insertFakeSequenceWithTrimmedFiles(token, files, strain2Id);
        final String referenceId = db.insertFakeReferenceWithFile(referenceFile, strain1Id);

        stubFor(post(urlEqualTo("/analysis")));

        given().
                spec(requestSpec).
                param("sequences", sequenceIds[0], sequenceIds[1], sequenceIds[2], sequenceIds[3]).
                param("reference", referenceId).
        when().
                post("/api/reports").
        then().
                statusCode(409);

        verify(exactly(0), postRequestedFor(urlEqualTo("/analysis")));
    }

    @Test
    public void given_aReference_and_lessThanFourSequences_when_postToReports_then_badRequest() throws IOException {
        final String token = token();
        final String strainId = db.insertFakeStrain("kp", "test");
        Collection<File> files = new ArrayList<>();
        files.add(new File(testFolderPath + "ngs/Kp4_R1_001_trimmed.fq.gz"));
        files.add(new File(testFolderPath + "ngs/Kp4_R2_001_trimmed.fq.gz"));
        File referenceFile = new File(testFolderPath + "Kpneu231120_referencia.fa");
        String[] sequenceIds = new String[3];
        for (int i = 0; i < 3; i++) {
            sequenceIds[i] = db.insertFakeSequenceWithTrimmedFiles(token, files, strainId);
        }
        final String referenceId = db.insertFakeReferenceWithFile(referenceFile, strainId);

        stubFor(post(urlEqualTo("/analysis")).willReturn(aResponse()));
        stubFor(patch(urlEqualTo("/analysis/" + token)).willReturn(aResponse()));
        stubFor(post(urlEqualTo("/analysis/" + token)).willReturn(aResponse()));

        given().
                spec(requestSpec).
                param("sequences", sequenceIds[0], sequenceIds[1], sequenceIds[2]).
                param("reference", referenceId).
        when().
                post("/api/reports").
        then().
                statusCode(400);

        verify(exactly(0), postRequestedFor(urlEqualTo("/analysis")));
        verify(exactly(0), patchRequestedFor(urlEqualTo("/analysis/" + token)));
        verify(exactly(0), postRequestedFor(urlEqualTo("/analysis/" + token)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void given_aReference_and_fourOrMoreSequences_when_postToReports_then_reportCreated_and_returnHttpAccepted() throws IOException {
        final String token = token();
        final String strainId = db.insertFakeStrain("kp", "test");
        Collection<File> files = new ArrayList<>();
        files.add(new File(testFolderPath + "ngs/Kp4_R1_001_trimmed.fq.gz"));
        files.add(new File(testFolderPath + "ngs/Kp4_R2_001_trimmed.fq.gz"));
        File referenceFile = new File(testFolderPath + "Kpneu231120_referencia.fa");
        String[] sequenceIds = new String[4];
        for (int i = 0; i < 4; i++) {
            sequenceIds[i] = db.insertFakeSequenceWithTrimmedFiles(token, files, strainId);
        }
        final String referenceId = db.insertFakeReferenceWithFile(referenceFile, strainId);

        stubFor(post(urlEqualTo("/analysis"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"" + token + "\"}")));

        stubFor(patch(urlEqualTo("/analysis/" + token))
                .willReturn(aResponse()
                        .withStatus(200)));

        stubFor(post(urlEqualTo("/analysis/" + token))
                .willReturn(aResponse()
                        .withStatus(202)));

        String response =
                given().
                        spec(requestSpec).
                        param("sequences", sequenceIds[0], sequenceIds[1], sequenceIds[2], sequenceIds[3]).
                        param("reference", referenceId).
                when().
                        post("/api/reports").
                then().
                        statusCode(202).
                        extract().
                        asString();

        System.out.println(response);

        verify(exactly(1), postRequestedFor(urlEqualTo("/analysis")));
        verify(exactly(9), patchRequestedFor(urlEqualTo("/analysis/" + token)));
        verify(exactly(1), postRequestedFor(urlEqualTo("/analysis/" + token)));

        ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
        String id = String.valueOf(node.get("id").asText());
        Map<String, Object> report = db.get("reports", id);
        Map<String, Object> strain = db.get("strains", "keys", "kp");

        String date = dateFormat(Calendar.getInstance().getTime(), "dd/MM/yyyy HH:mm");

        assertThat(report.get("name")).isEqualTo(strain.get("name") + " " + date);
        assertThat(report.get("strain")).isEqualTo(strain.get("_id"));

        assertThat(report.get("sequences")).isOfAnyClassIn(ArrayList.class);
        ArrayList<Map<String, String>> sequences = (ArrayList<Map<String, String>>) report.get("sequences");
        assertThat(sequences.size()).isEqualTo(4);
        assertThat(sequences).containsExactlyInAnyOrder(
                Map.of("$oid", sequenceIds[0]),
                Map.of("$oid", sequenceIds[1]),
                Map.of("$oid", sequenceIds[2]),
                Map.of("$oid", sequenceIds[3]));

        LinkedHashMap<String, String> reference = (LinkedHashMap<String, String>) report.get("reference");
        assertThat(reference.get("$oid")).isEqualTo(referenceId);
        assertThat(report.get("genomeToolToken")).isEqualTo(token);
        assertThat(report.get("requestDate").toString()).isEqualTo(date);
        assertThat(report.get("files")).isNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void given_aSuccessfulStatus_when_postToReportsResult_then_uploadResultFiles_and_associateThemToTheirReport() throws IOException {
        final String token = token();
        final String strainId = db.insertFakeStrain("kp");
        final String reportId = db.insertFakeReport(token, strainId);

        given().
                spec(requestSpec).
                multiPart("status", 2).
                multiPart("token", token).
                multiPart("file1", new File(testFolderPath + "informe.html")).
                multiPart("file2", new File(testFolderPath + "file.fa")).
        when().
                post("/api/reports/result").
        then().
                statusCode(200);

        Map<String, Object> report = db.get("reports", reportId);
        assertThat(report.get("files")).isNotNull();
        assertThat(report.get("files")).isNotEqualTo(false);
        assertThat(report.get("files")).isOfAnyClassIn(LinkedHashMap.class);

        Map<String, Object> files = (Map<String, Object>) report.get("files");
        assertThat(files.get("report")).isOfAnyClassIn(LinkedHashMap.class);
        assertThat(files.get("reference")).isOfAnyClassIn(LinkedHashMap.class);

        String referenceId = ((LinkedHashMap<String, String>) files.get("reference")).get("$oid");
        Map<String, Object> reference = db.get("references", referenceId);
        assertThat(reference).isNotNull();
        assertThat(((LinkedHashMap<String, String>) reference.get("strain")).get("$oid")).isEqualTo(strainId);
        assertThat(reference.get("file")).isOfAnyClassIn(LinkedHashMap.class);
        assertThat(reference.get("reportName")).isEqualTo(report.get("name"));
        assertThat(files.get("reference"))
                .isEqualTo(reference.get("_id"));
    }

    @Test
    public void given_anErrorStatusAndLogFile_when_postToReportsResult_then_setFileFieldToFalseAndAppendLog() throws IOException {
        final String token = token();
        final String id = db.insertFakeReport(token, db.insertFakeStrain("kp"));

        given().
                spec(requestSpec).
                multiPart("status", 5).
                multiPart("token", token).
                multiPart("file", new File(testFolderPath + "nullarbor.log")).
        when().
                post("/api/reports/result").
        then().
                statusCode(200);

        Map<String, Object> sequence = db.get("reports", id);
        assertThat(sequence.get("files")).isEqualTo(false);
        assertThat(sequence.get("log")).isOfAnyClassIn(LinkedHashMap.class);
    }

    @Test
    public void given_anErrorStatus_when_postToReportsResult_then_setFileFieldToFalse() throws IOException {
        final String token = token();
        final String id = db.insertFakeReport(token, db.insertFakeStrain("kp"));

        given().
                spec(requestSpec).
                multiPart("status", 5).
                multiPart("token", token).
        when().
                post("/api/reports/result").
        then().
                statusCode(200);

        Map<String, Object> sequence = db.get("reports", id);
        assertThat(sequence.get("files")).isEqualTo(false);
        assertThat(sequence.get("log")).isNull();
    }

    @Test
    public void when_getToReports_then_returnAJsonOfAllReports() throws IOException {
        final String strainId = db.insertFakeStrain("kp");
        final int amount = 5;
        for (int i = 0; i < amount; i++) db.insertFakeReport(token(), strainId);

        String response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/reports").
                then().
                        statusCode(200).
                        extract().asString();

        List<Object> reports = Arrays.asList(new ObjectMapper().readValue(response, Object[].class));
        assertThat(reports.size()).isEqualTo(amount);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void when_getToReportsId_then_returnReportWithGivenIdAsJson() throws IOException {
        final String token = token();
        final String id = db.insertFakeReport(token, db.insertFakeStrain("kp"));

        String response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/reports/" + id).
                then().
                        statusCode(200).
                        extract().asString();

        Map<String, Object> sequenceJson = new ObjectMapper().readValue(response, new TypeReference<HashMap<String,Object>>(){});
        assertThat(((Map<String, String>) sequenceJson.get("_id")).get("$oid"))
                .isEqualTo(id);
        assertThat(sequenceJson.get("genomeToolToken")).isEqualTo(token);
    }

    @Test
    public void when_getToReportsIdFile_then_returnReportFile() throws IOException {
        File file = new File(testFolderPath + "informe.html");
        String id = db.insertFakeReportWithFile(file);

        byte[] response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/reports/" + id + "/file").
                then().
                        statusCode(200).
                        contentType("text/html").
                        header("Content-Disposition", "attachment; filename=file.html").
                        extract().asByteArray();

        InputStream responseStream = new ByteArrayInputStream(response);
        assertThat(IOUtils.contentEquals(responseStream, new FileInputStream(file))).isTrue();
    }

    @Test
    public void when_getToReportsIdFile_and_fileDoesNotExist_then_returnHttp404() {
        String id = db.insertFakeReport(token(), db.insertFakeStrain("kp"));
        given().
                spec(requestSpec).
        when().
                get("/api/reports/" + id + "/file").
        then().
                statusCode(404);
    }

    @Test
    public void when_getToReportsIdLog_then_returnLogFile() throws IOException {
        File file = new File(testFolderPath + "nullarbor.log");
        String id = db.insertFakeReportWithLog(file);

        byte[] response =
                given().
                        spec(requestSpec).
                when().
                        get("/api/reports/" + id + "/log").
                then().
                        statusCode(200).
                        contentType("text/plain").
                        header("Content-Disposition", "attachment; filename=file.log").
                        extract().asByteArray();

        InputStream responseStream = new ByteArrayInputStream(response);
        assertThat(IOUtils.contentEquals(responseStream, new FileInputStream(file))).isTrue();
    }

    @BeforeAll
    static void startApplication() throws IOException, InterruptedException, InvalidKeySpecException, NoSuchAlgorithmException {
        port = PORT;
        ProcessBuilder process = new ProcessBuilder(
                "test/start_application.sh",
                String.valueOf(PORT),
                String.valueOf(DB_PORT),
                "http://localhost:" + WIREMOCK_PORT);
        process.start();

        int attemptsLeft = 10;
        while (attemptsLeft-- > 0) {
            if (theApplicationIsRunning()) continue;
            System.out.println("Waiting for the application... (attemps left "+attemptsLeft+")");
            Thread.sleep(1000);
        }

        authSetup();
    }

    @BeforeAll
    static void startCleanDatabase() throws IOException {
        ProcessBuilder process = new ProcessBuilder("test/start_db.sh", String.valueOf(DB_PORT));
        process.start();
        db = new MongoDB(DB_PORT);
    }

    private static void authSetup() throws InvalidKeySpecException, NoSuchAlgorithmException {
        db.createUser();

        Response body =
                given().
                        param("username", "test").
                        param("password", "password").
                when().
                        post("/api/login").
                then().
                        statusCode(200).extract().response();

        System.out.println(body.asPrettyString());
        String token = body.path("token").toString();
        String Authorization = "Bearer " + token;

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.addHeader("Authorization", Authorization);
        requestSpec = builder.build();
    }

    private static boolean theApplicationIsRunning() {
        try {
            RestAssured.get("/api/alive");
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

    @AfterEach
    public void cleanDatabase() {
        db.empty("sequences");
        db.empty("references");
        db.empty("strains");
        db.empty("reports");
    }

    @BeforeEach
    public void startWireMockServer() {
        mockServer = new WireMockServer(options()
                .port(WIREMOCK_PORT));
        mockServer.start();
        configureFor(mockServer.port());
    }

    @AfterEach
    public void stopWireMockServer() {
        mockServer.stop();
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

    private String token() {
        return UUID.randomUUID().toString();
    }

    private void insertFakeSequences(int amount) {
        for (int i = 0; i < amount; i++) db.insertFakeSequence(token());
    }

    private void insertFakeSequences(int amount, String strainId) {
        for (int i = 0; i < amount; i++) db.insertFakeSequence(token(), strainId);
    }

    private void insertFakeReferences(int amount) {
        for (int i = 0; i < amount; i++) db.insertFakeReference();
    }

    private void insertFakeReferences(int amount, String strainId) {
        for (int i = 0; i < amount; i++) db.insertFakeReference(strainId);
    }
}
