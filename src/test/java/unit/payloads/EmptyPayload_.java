package unit.payloads;

import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;

import static org.assertj.core.api.Assertions.assertThat;

public class EmptyPayload_ {
    @Test
    public void isAlwaysValid() {
        assertThat(new EmptyPayload().isValid()).isEqualTo(true);
    }
}
