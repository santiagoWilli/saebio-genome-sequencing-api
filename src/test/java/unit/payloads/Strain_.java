package unit.payloads;

import org.junit.jupiter.api.Test;
import payloads.Strain;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Strain_ {
    @Test
    public void valid_if_requestHasKeyAndName() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("key", "");
        parameters.put("name", "");
        Strain strain = new Strain(parameters);
        assertThat(strain.isValid()).isTrue();
    }

    @Test
    public void invalid_if_requestHasNotKeyOrName() {
        Strain strain;
        Map<String, String> parameters;

        parameters = new HashMap<>();
        parameters.put("key", "");
        strain = new Strain(parameters);
        assertThat(strain.isValid()).isFalse();

        parameters = new HashMap<>();
        parameters.put("name", "");
        strain = new Strain(parameters);
        assertThat(strain.isValid()).isFalse();
    }
}
