package unit;

import com.github.tomakehurst.wiremock.WireMockServer;
import genome.GenomeToolAnswer;
import genome.NullarborClient;
import org.junit.jupiter.api.*;
import payloads.Sequence;

import javax.servlet.http.Part;

import java.io.*;
import java.util.*;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class NullarborClient_ {
    private WireMockServer mockServer;
    private Sequence sequence;
    private Collection<Part> parts;
    private NullarborClient client;

    @Test
    public void exceptionBeforeApiCall_doesNotCallTheApi_and_returns_exceptionEncounteredCode() throws IOException {
        for (int i = 0; i < 2; i++) {
            Part part = mock(Part.class);
            when(part.getInputStream()).thenThrow(IOException.class);
            parts.add(part);
        }

        assertThat(client.requestTrim(sequence).getStatus()).isEqualTo(GenomeToolAnswer.Status.EXCEPTION_ENCOUNTERED);
        verify(exactly(0), postRequestedFor(urlEqualTo("/trim")));
    }

    @Test
    public void httpNotFound_returns_apiDownCode() throws IOException {
        mockCollectionOfParts();

        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(404)));
        assertThat(client.requestTrim(sequence).getStatus()).isEqualTo(GenomeToolAnswer.Status.API_DOWN);
        verify(exactly(1), postRequestedFor(urlEqualTo("/trim")));
    }

    @Test
    public void httpInternalServerError_returns_externalApiErrorCode() throws IOException {
        mockCollectionOfParts();

        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(500)));
        assertThat(client.requestTrim(sequence).getStatus()).isEqualTo(GenomeToolAnswer.Status.SERVER_ERROR);
        verify(exactly(1), postRequestedFor(urlEqualTo("/trim")));
    }

    @Test
    public void httpAccepted_returns_okCode_and_nullarborToken() throws IOException {
        mockCollectionOfParts();
        String token = "123e4567-e89b-12d3-a456-556642440000";

        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"" + token + "\"}")));
        GenomeToolAnswer clientAnswer = client.requestTrim(sequence);
        assertThat(clientAnswer).isEqualTo(new GenomeToolAnswer(GenomeToolAnswer.Status.OK, token));
        verify(exactly(1), postRequestedFor(urlEqualTo("/trim"))
                .withHeader("Content-Type", containing("multipart/form-data"))
                .withRequestBodyPart(aMultipart().withName("pair1").build())
                .withRequestBodyPart(aMultipart().withName("pair2").build()));
    }

    @BeforeEach
    public void objectsSetUp() {
        parts = new ArrayList<>();
        sequence = mock(Sequence.class);
        when(sequence.getParts()).thenReturn(parts);
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

    private void mockCollectionOfParts() throws IOException {
        for (int i = 0; i < 2; i++) addPartToCollectionOfParts();
    }

    private void addPartToCollectionOfParts() throws IOException {
        InputStream inputStream = new FileInputStream("test/resources/sequences/Kpneu1_191120_R1.fastq.gz");
        Part part = mock(Part.class);
        when(part.getInputStream()).thenReturn(inputStream);
        parts.add(part);
    }
}
