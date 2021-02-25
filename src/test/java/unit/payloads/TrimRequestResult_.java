package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.TrimRequestResult;

import javax.servlet.http.Part;
import java.io.*;
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
        multipart.add(statusPart("5"));
        multipart.add(mockedPartWithName("token"));

        result = new TrimRequestResult(multipart);
        assertThat(result.isValid()).isEqualTo(true);
    }

    @Test
    public void invalid_if_multipartWithoutStatusOrTokenFields() {
        result = new TrimRequestResult(multipartWithoutToken());
        assertThat(result.isValid()).isEqualTo(false);
        result = new TrimRequestResult(multipartWithoutStatus());
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void invalid_if_successStatus_and_hasAMissingTrimmedFile() throws IOException {
        multipart.add(statusPart("2"));
        multipart.add(mockedPartWithName("token"));
        multipart.add(mockedPartWithName("file1"));

        result = new TrimRequestResult(multipart);
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void valid_if_successStatus_and_hasTheTwoTrimmedFiles_and_fileTypeIsFastq() throws IOException {
        multipart.add(statusPart("2"));
        multipart.add(mockedPartWithName("token"));
        multipart.add(mockedFilePart("file1", "trimmed1.fq.gz"));
        multipart.add(mockedFilePart("file2", "trimmed2.fq.gz"));

        result = new TrimRequestResult(multipart);
        assertThat(result.isValid()).isEqualTo(true);
    }

    @Test
    public void invalid_if_successStatus_and_hasTheTwoTrimmedFiles_and_fileTypeDifferentFromFastq() throws IOException {
        multipart.add(statusPart("2"));
        multipart.add(mockedPartWithName("token"));
        multipart.add(mockedFilePart("file1", "trimmed1.pdf"));
        multipart.add(mockedFilePart("file2", "trimmed2.fa.gz"));

        result = new TrimRequestResult(multipart);
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void invalid_if_successStatus_and_hasTheTwoTrimmedFilesWithSameName() throws IOException {
        multipart.add(statusPart("2"));
        multipart.add(mockedPartWithName("token"));
        multipart.add(mockedFilePart("file1", "trimmed1.fq.gz"));
        multipart.add(mockedFilePart("file2", "trimmed1.fq.gz"));

        result = new TrimRequestResult(multipart);
        assertThat(result.isValid()).isEqualTo(false);
    }

    private Part mockedFilePart(String partName, String filename) {
        Part part = mockedPartWithName(partName);
        when(part.getSubmittedFileName()).thenReturn(filename);
        return part;
    }

    private Part statusPart(String s) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(s.getBytes());
        Part part = mockedPartWithName("status");
        when(part.getInputStream()).thenReturn(inputStream);
        return part;
    }

    private Collection<Part> multipartWithoutToken() {
        Collection<Part> multipart = new ArrayList<>();
        multipart.add(mockedPartWithName("status"));
        return multipart;
    }

    private Collection<Part> multipartWithoutStatus() {
        multipart.add(mockedPartWithName("token"));
        return multipart;
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
