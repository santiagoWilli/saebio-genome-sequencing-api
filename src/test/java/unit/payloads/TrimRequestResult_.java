package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
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
    private Collection<Part> multipart;
    TrimRequestResult result;

    @BeforeEach
    public void setup() {
        multipart = new ArrayList<>();
    }

    @Test
    public void getSequenceToken_returns_theTokenField() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(token().getBytes());
        Part part = mock(Part.class);
        when(part.getName()).thenReturn("token");
        when(part.getInputStream()).thenReturn(inputStream);
        multipart.add(part);

        result = new TrimRequestResult(multipart);
        assertThat(result.getSequenceToken()).isEqualTo(token());
    }

    @Test
    public void valid_if_failureStatus_and_hasTokenField() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("5".getBytes());
        Part part;
        part = mockedPartWithName("status");
        when(part.getInputStream()).thenReturn(inputStream);
        multipart.add(part);
        multipart.add(mockedPartWithName("token"));

        result = new TrimRequestResult(multipart);
        assertThat(result.isValid()).isEqualTo(true);
    }

    private String token() {
        return "123e4567-e89b-12d3-a456-556642440000";
    }

    private Part mockedPartWithName(String name) {
        Part part = mock(Part.class);
        when(part.getName()).thenReturn(name);
        return part;
    }
}
