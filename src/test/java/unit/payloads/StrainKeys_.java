package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.StrainKeys;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StrainKeys_ {
    private Map<String, String[]> parameters;
    private StrainKeys keys;

    @Test
    public void valid_if_requestHasAtLeastOneKey_and_isNotEmpty() {
        parameters.put("key", new String[]{"kneu"});
        assertThat(keys.isValid()).isTrue();
    }

    @Test
    public void invalid_if_requestHasNoKeys() {
        parameters.put("anything", new String[]{"kneu"});
        assertThat(keys.isValid()).isFalse();
    }

    @Test
    public void invalid_if_requestHasAtLeastOneKeyButTheyAreEmpty() {
        parameters.put("key", new String[]{""});
        assertThat(keys.isValid()).isFalse();
    }

    @Test
    public void invalid_if_requestKeysHaveNonAlphabeticCharacters() {
        parameters.put("key", new String[]{"kp", "k_neu"});
        assertThat(keys.isValid()).isFalse();
    }

    @BeforeEach
    public void setUp() {
        parameters = new HashMap<>();
        keys = new StrainKeys(parameters);
    }
}
