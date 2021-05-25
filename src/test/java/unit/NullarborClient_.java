package unit;

import com.github.tomakehurst.wiremock.WireMockServer;
import genome.GenomeToolAnswer;
import genome.NullarborClient;
import org.junit.jupiter.api.*;
import payloads.Sequence;

import java.io.*;
import java.util.*;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class NullarborClient_ {
    private WireMockServer mockServer;
    private Sequence sequence;
    private Map<String, File> files;
    private NullarborClient client;

    @Test
    public void httpNotFound_returns_apiDownCode() {
        mockFileMap();

        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(404)));
        assertThat(client.requestTrim(sequence).getStatus()).isEqualTo(GenomeToolAnswer.Status.API_DOWN);
        verify(exactly(1), postRequestedFor(urlEqualTo("/trim")));
    }

    @Test
    public void httpInternalServerError_returns_externalApiErrorCode() {
        mockFileMap();

        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(500)));
        assertThat(client.requestTrim(sequence).getStatus()).isEqualTo(GenomeToolAnswer.Status.SERVER_ERROR);
        verify(exactly(1), postRequestedFor(urlEqualTo("/trim")));
    }

    @Test
    public void requestTrim_httpAccepted_returns_okCode_and_nullarborToken() {
        mockFileMap();
        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"" + token() + "\"}")));
        GenomeToolAnswer clientAnswer = client.requestTrim(sequence);
        assertThat(clientAnswer).isEqualTo(new GenomeToolAnswer(GenomeToolAnswer.Status.OK, token()));
        verify(exactly(1), postRequestedFor(urlEqualTo("/trim"))
                .withHeader("Content-Type", containing("multipart/form-data"))
                .withRequestBodyPart(aMultipart().withName("pair1").build())
                .withRequestBodyPart(aMultipart().withName("pair2").build()));
    }

    @Test
    public void requestToSendAnalysisFiles_httpAccepted_returns_okCode_and_analysisToken() {
        stubFor(post(urlEqualTo("/analysis"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"" + token() + "\"}")));
        GenomeToolAnswer clientAnswer = client.requestToSendAnalysisFiles();
        assertThat(clientAnswer).isEqualTo(new GenomeToolAnswer(GenomeToolAnswer.Status.OK, token()));
        verify(exactly(1), postRequestedFor(urlEqualTo("/analysis")));
    }

    @Test
    public void sendFile_httpAccepted_returns_okCode_and_analysisToken() throws FileNotFoundException {
        stubFor(patch(urlEqualTo("/analysis/" + token())).willReturn(aResponse().withStatus(200)));

        InputStream stream = new FileInputStream("test/resources/sequences/Kpneu231120_referencia.fa");
        GenomeToolAnswer clientAnswer = client.sendAnalysisFile(token(), stream, "name");
        assertThat(clientAnswer).isEqualTo(new GenomeToolAnswer(GenomeToolAnswer.Status.OK));
        verify(exactly(1), patchRequestedFor(urlEqualTo("/analysis/" + token()))
                .withHeader("Content-Type", containing("multipart/form-data"))
                .withRequestBodyPart(aMultipart().withName("file").build()));
    }

    @Test
    public void requestToStartAnalysis_httpAccepted_returns_okCode_and_analysisToken() {
        stubFor(post(urlEqualTo("/analysis/" + token())).willReturn(aResponse().withStatus(202)));
        GenomeToolAnswer clientAnswer = client.requestToStartAnalysis(token());
        assertThat(clientAnswer).isEqualTo(new GenomeToolAnswer(GenomeToolAnswer.Status.OK));
        verify(exactly(1), postRequestedFor(urlEqualTo("/analysis/" + token())));
    }

    @BeforeEach
    public void objectsSetUp() {
        files = new HashMap<>();
        sequence = mock(Sequence.class);
        when(sequence.getFiles()).thenReturn(files);
    }

    @BeforeEach
    public void startWireMockServer() {
        mockServer = new WireMockServer(options()
                .dynamicPort());
        mockServer.start();
        configureFor(mockServer.port());
        client = new NullarborClient(mockServer.baseUrl());
    }

    @AfterEach
    public void stopWireMockServer() {
        mockServer.stop();
    }

    private void mockFileMap() {
        for (int i = 0; i < 2; i++) addMockedEntryToFileMap(i);
    }

    private void addMockedEntryToFileMap(int i) {
        files.put(Integer.toString(i), new File("test/resources/sequences/Kpneu1_191120_R1.fastq.gz"));
    }

    private String token() {
        return "123e4567-e89b-12d3-a456-556642440000";
    }
}
