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
    public void invalid_if_parametersDoesNotHaveSequences() {
        assertThat(reportRequest.isValid()).isFalse();
    }

    @Test
    public void valid_if_sequencesListIsGreaterThanThree_and_parametersHasAReference() {
        parameters.put("sequences", new String[]{"1", "2", "3", "4"});
        parameters.put("reference", new String[]{"1"});
        assertThat(reportRequest.isValid()).isTrue();
    }

    @Test
    public void invalid_if_sequencesListIsEmpty() {
        parameters.put("sequences", new String[]{"", ""});
        assertThat(reportRequest.isValid()).isFalse();
    }

    @Test
    public void invalid_if_parametersDoesNotHaveAReference() {
        parameters.put("sequences", new String[]{"1", "2", "3"});
        assertThat(reportRequest.isValid()).isFalse();
    }

    @Test
    public void invalid_if_multipleReferences() {
        parameters.put("sequences", new String[]{"1"});
        parameters.put("reference", new String[]{"1", "2"});
        assertThat(reportRequest.isValid()).isFalse();
    }

    @Test
    public void invalid_if_referenceIsEmpty() {
        parameters.put("sequences", new String[]{"1"});
        parameters.put("reference", new String[]{""});
        assertThat(reportRequest.isValid()).isFalse();
    }

    @Test
    public void invalid_if_lessThanFourSequences() {
        parameters.put("sequences", new String[]{"1", "2", "3"});
        parameters.put("reference", new String[]{"1"});
        assertThat(reportRequest.isValid()).isFalse();
    }

    @Test
    public void getSequences_should_returnASetWithThePassedIds() {
        parameters.put("sequences", new String[]{"1", "2", "2", "1", "2"});
        assertThat(reportRequest.getSequences()).containsExactlyInAnyOrder("1", "2");
    }

    @Test
    public void getReference_should_returnTheIdOfTheFirstPassedReference() {
        parameters.put("reference", new String[]{"1", "2"});
        assertThat(reportRequest.getReference()).isEqualTo("1");
    }

    @BeforeEach
    public void setUp() {
        parameters = new HashMap<>();
        reportRequest = new ReportRequest(parameters);
    }
}
