package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Strain;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Strain_ {
    private Map<String, String[]> parameters;
    private Strain strain;

    @Test
    public void valid_if_requestHasKeyAndName_and_areNotEmpty() {
        parameters.put("key", new String[]{"kneu"});
        parameters.put("name", new String[]{"klebsiella"});
        assertThat(strain.isValid()).isTrue();
    }

    @Test
    public void invalid_if_requestHasKeyAndName_and_areEmpty() {
        parameters.put("key", new String[]{""});
        parameters.put("name", new String[]{""});
        assertThat(strain.isValid()).isFalse();
    }

    @Test
    public void invalid_if_requestHasKeyAndName_and_keyIsNotComposedOfOnlyAlphabeticCharacters() {
        parameters.put("name", new String[]{"klebsi"});
        parameters.put("key", new String[]{"k_neu"});
        assertThat(strain.isValid()).isFalse();
        parameters.put("key", new String[]{"key*"});
        assertThat(strain.isValid()).isFalse();
        parameters.put("key", new String[]{"any key"});
        assertThat(strain.isValid()).isFalse();
        parameters.put("key", new String[]{"key1"});
        assertThat(strain.isValid()).isFalse();
        parameters.put("key", new String[]{""});
        assertThat(strain.isValid()).isFalse();
    }

    @Test
    public void invalid_if_requestHasNotKeyOrName() {
        parameters.put("key", new String[]{"key"});
        assertThat(strain.isValid()).isFalse();

        parameters.clear();
        parameters.put("name", new String[]{"name"});
        assertThat(strain.isValid()).isFalse();
    }

    @Test
    public void getName_shouldReturn_nameParameter() {
        parameters.put("name", new String[]{"Klebsiella pneumoniae"});
        assertThat(strain.getName()).isEqualTo("Klebsiella pneumoniae");
    }

    @Test
    public void getKeys_shouldReturn_keysInLowerCase() {
        parameters.put("key", new String[]{"kNEU", "kp"});
        assertThat(strain.getKeys()).containsExactly("kneu", "kp");
    }

    @BeforeEach
    public void setUp() {
        parameters = new HashMap<>();
        strain = new Strain(parameters);
    }
}
