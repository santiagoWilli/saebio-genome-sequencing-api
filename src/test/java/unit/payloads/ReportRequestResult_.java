package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.ReportRequestResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ReportRequestResult_ {
    private Map<String, String> fields;
    private Map<String, File> files;
    private ReportRequestResult result;

    @BeforeEach
    public void setup() {
        fields = new HashMap<>();
        files = new HashMap<>();
        result = new ReportRequestResult(fields, files);
    }

    @Test
    public void valid_if_failureStatus_and_hasTokenField() {
        fields.put("status", "5");
        fields.put("token", token());
        assertThat(result.isValid()).isEqualTo(true);
    }

    @Test
    public void invalid_if_noStatusOrTokenFields() {
        fields.put("status", "5");
        assertThat(result.isValid()).isEqualTo(false);

        fields.clear();
        fields.put("token", token());
        result = new ReportRequestResult(fields, files);
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void invalid_if_successStatus_and_hasNoFile() {
        fields.put("status", "2");
        fields.put("token", token());
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void valid_if_successStatus_and_hasHtmlFile() {
        fields.put("status", "2");
        fields.put("token", token());
        files.put("index.html", mock(File.class));
        assertThat(result.isValid()).isEqualTo(true);
    }

    @Test
    public void valid_if_successStatus_and_hasFileButNotHtml() {
        fields.put("status", "2");
        fields.put("token", token());
        files.put("index.fa", mock(File.class));
        assertThat(result.isValid()).isEqualTo(false);
    }

    private String token() {
        return "123e4567-e89b-12d3-a456-556642440000";
    }
}
