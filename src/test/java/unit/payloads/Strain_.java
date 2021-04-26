package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Strain;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Strain_ {
    private Map<String, String> parameters;
    private Strain strain;

    @Test
    public void valid_if_requestHasKeyAndName_and_areNotEmpty() {
        parameters.put("key", "kneu");
        parameters.put("name", "klebsiella");
        assertThat(strain.isValid()).isTrue();
    }

    @Test
    public void invalid_if_requestHasKeyAndName_and_areEmpty() {
        parameters.put("key", "");
        parameters.put("name", "");
        assertThat(strain.isValid()).isFalse();
    }

    @Test
    public void invalid_if_requestHasKeyAndName_and_keyIsNotComposedOfOnlyAlphabeticCharacters() {
        parameters.put("name", "klebsi");
        parameters.put("key", "k_neu");
        assertThat(strain.isValid()).isFalse();
        parameters.put("key", "key*");
        assertThat(strain.isValid()).isFalse();
        parameters.put("key", "any key");
        assertThat(strain.isValid()).isFalse();
        parameters.put("key", "key1");
        assertThat(strain.isValid()).isFalse();
    }

    @Test
    public void invalid_if_requestHasNotKeyOrName() {
        parameters.put("key", "");
        assertThat(strain.isValid()).isFalse();

        parameters.clear();
        parameters.put("name", "");
        assertThat(strain.isValid()).isFalse();
    }

    @Test
    public void getName_shouldReturn_nameParameter() {
        parameters.put("name", "Klebsiella pneumoniae");
        assertThat(strain.getName()).isEqualTo("Klebsiella pneumoniae");
    }

    @Test
    public void getKey_shouldReturn_keyParameterAlwaysInLowerCase() {
        parameters.put("key", "kNEU");
        assertThat(strain.getKey()).isEqualTo("kneu");
    }

    @BeforeEach
    public void setUp() {
        parameters = new HashMap<>();
        strain = new Strain(parameters);
    }
}
