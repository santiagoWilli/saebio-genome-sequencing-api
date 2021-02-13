package unit;

import com.github.tomakehurst.wiremock.WireMockServer;
import genome.GenomeTool;
import genome.NullarborClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Sequence;

import javax.servlet.http.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class NullarborClient_ {
    private WireMockServer mockServer;

    @Test
    public void exceptionBeforeApiCall_doesNotCallTheApi() throws IOException {
        Collection<Part> parts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Part part = mock(Part.class);
            when(part.getInputStream()).thenThrow(IOException.class);
            parts.add(part);
        }
        Sequence sequence = mock(Sequence.class);
        when(sequence.isValid()).thenReturn(true);
        when(sequence.getFileParts()).thenReturn(parts);

        GenomeTool client = new NullarborClient();
        assertThat(client.requestTrim(sequence).getCode()).isEqualTo(GenomeTool.Response.EXCEPTION_ENCOUNTERED.code());
        verify(exactly(0), postRequestedFor(urlEqualTo("/trim")));
    }

    @BeforeEach
    public void startWireMockServer() {
        mockServer = new WireMockServer(options()
                .dynamicPort());
        mockServer.start();
        configureFor(this.mockServer.port());
    }

    @AfterEach
    public void stopWireMockServer() {
        mockServer.stop();
    }
}
