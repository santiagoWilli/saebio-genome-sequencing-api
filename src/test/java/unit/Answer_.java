package unit;

import handlers.Answer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Answer_ {
    @Test
    public void badRequest_returns_code400() {
        assertThat(Answer.badRequest()).isEqualTo(400);
    }
}
