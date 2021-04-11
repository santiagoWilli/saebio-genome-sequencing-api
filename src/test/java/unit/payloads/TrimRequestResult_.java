package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.TrimRequestResult;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TrimRequestResult_ {
    private Map<String, String> fields;
    private Map<String, File> files;
    private TrimRequestResult result;

    @BeforeEach
    public void setup() {
        fields = new HashMap<>();
        files = new HashMap<>();
    }

    @Test
    public void getSequenceToken_returns_theTokenField() {
        fields.put("token", token());
        result = new TrimRequestResult(fields, files);
        assertThat(result.getSequenceToken()).isEqualTo(token());
    }

    @Test
    public void valid_if_failureStatus_and_hasTokenField() {
        fields.put("status", "5");
        fields.put("token", token());

        result = new TrimRequestResult(fields, files);
        assertThat(result.isValid()).isEqualTo(true);
    }

    @Test
    public void invalid_if_multipartWithoutStatusOrTokenFields() {
        fields.put("status", "5");
        result = new TrimRequestResult(fields, null);
        assertThat(result.isValid()).isEqualTo(false);

        fields.clear();
        fields.put("token", token());
        result = new TrimRequestResult(fields, files);
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void invalid_if_successStatus_and_hasAMissingTrimmedFile() {
        fields.put("status", "2");
        fields.put("token", token());
        files.put("trimmed1.fastq", mock(File.class));

        result = new TrimRequestResult(fields, files);
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void valid_if_successStatus_and_hasTheTwoTrimmedFiles_and_fileTypeIsFastq() {
        fields.put("status", "2");
        fields.put("token", token());
        files.put("trimmed1.fastq", mock(File.class));
        files.put("trimmed2.fastq", mock(File.class));

        result = new TrimRequestResult(fields, files);
        assertThat(result.isValid()).isEqualTo(true);
    }

    @Test
    public void invalid_if_successStatus_and_hasTheTwoTrimmedFiles_and_fileTypeDifferentFromFastq() {
        fields.put("status", "2");
        fields.put("token", token());
        files.put("trimmed1.pdf", mock(File.class));
        files.put("trimmed2.fa", mock(File.class));

        result = new TrimRequestResult(fields, files);
        assertThat(result.isValid()).isEqualTo(false);
    }


    @Test
    public void invalid_if_successStatus_and_hasTheTwoTrimmedFilesWithSameName() {
        fields.put("status", "2");
        fields.put("token", token());
        files.put("trimmed1.fastq", mock(File.class));
        files.put("trimmed1.fastq", mock(File.class));

        result = new TrimRequestResult(fields, files);
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void getStatusCode_returns_statusCodeAsInt() {
        fields.put("status", "5");

        result = new TrimRequestResult(fields, files);
        assertThat(result.getStatusCode()).isEqualTo(5);
    }

    private String token() {
        return "123e4567-e89b-12d3-a456-556642440000";
    }
}
