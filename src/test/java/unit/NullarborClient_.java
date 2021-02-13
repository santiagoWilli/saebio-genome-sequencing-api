package unit;

import com.github.tomakehurst.wiremock.WireMockServer;
import genome.GenomeTool;
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
    public void exceptionBeforeApiCall_doesNotCallTheApi() throws IOException {
        for (int i = 0; i < 2; i++) {
            Part part = mock(Part.class);
            when(part.getInputStream()).thenThrow(IOException.class);
            parts.add(part);
        }

        assertThat(client.requestTrim(sequence).getCode()).isEqualTo(GenomeTool.Response.EXCEPTION_ENCOUNTERED.code());
        verify(exactly(0), postRequestedFor(urlEqualTo("/trim")));
    }

    @Test
    public void httpNotFound_returns_apiDownCode() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        for (int i = 0; i < 2; i++) {
            Part part = mock(Part.class);
            when(part.getInputStream()).thenReturn(inputStream);
            parts.add(part);
        }

        stubFor(post(urlEqualTo("/trim"))
                .willReturn(aResponse()
                        .withStatus(404)));
        assertThat(client.requestTrim(sequence).getCode()).isEqualTo(GenomeTool.Response.API_DOWN.code());
        verify(exactly(1), postRequestedFor(urlEqualTo("/trim")));
    }

    @BeforeEach
    public void objectsSetUp() {
        parts = new ArrayList<>();
        sequence = mock(Sequence.class);
        when(sequence.getFileParts()).thenReturn(parts);
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
}
