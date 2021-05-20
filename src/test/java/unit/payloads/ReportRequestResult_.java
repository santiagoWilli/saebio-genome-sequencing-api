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
    private Map<String, File> file;
    private ReportRequestResult result;

    @BeforeEach
    public void setup() {
        fields = new HashMap<>();
        file = new HashMap<>();
        result = new ReportRequestResult(fields, file);
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
        result = new ReportRequestResult(fields, file);
        assertThat(result.isValid()).isEqualTo(false);
    }

    private String token() {
        return "123e4567-e89b-12d3-a456-556642440000";
    }
}
