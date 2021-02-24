package unit.payloads;

import org.junit.jupiter.api.Test;
import payloads.TrimRequestResult;

import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrimRequestResult_ {
    @Test
    public void getSequenceToken_returns_theTokenField() throws IOException {
        TrimRequestResult result = new TrimRequestResult(requestBody());
        assertThat(result.getSequenceToken()).isEqualTo(token());
    }

    private Collection<Part> requestBody() throws IOException {
        Collection<Part> multipart = new ArrayList<>();

        InputStream targetStream = new ByteArrayInputStream(token().getBytes());

        Part part = mock(Part.class);
        when(part.getName()).thenReturn("token");
        when(part.getInputStream()).thenReturn(targetStream);
        multipart.add(part);
        return multipart;
    }

    private String token() {
        return "123e4567-e89b-12d3-a456-556642440000";
    }
}
