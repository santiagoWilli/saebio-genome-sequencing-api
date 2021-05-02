package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.ReportRequest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportRequest_ {
    private Map<String, String[]> parameters;
    private ReportRequest reportRequest;

    @Test
    public void invalid_if_parametersDoesNotHaveIds() {
        assertThat(reportRequest.isValid()).isFalse();
    }

    @Test
    public void valid_if_sequencesListIsNotEmpty() {
        parameters.put("sequences", new String[]{"1", "2", "3"});
        assertThat(reportRequest.isValid()).isTrue();
    }

    @BeforeEach
    public void setUp() {
        parameters = new HashMap<>();
        reportRequest = new ReportRequest(parameters);
    }
}
