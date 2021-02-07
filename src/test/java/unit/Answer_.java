package unit;

import handlers.Answer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Answer_ {
    @Test
    public void badRequest_returns_code400() {
        Answer answer = Answer.badRequest("The request is invalid");
        assertThat(answer.getCode()).isEqualTo(400);
        assertThat(answer.getBody()).isEqualTo("{\"message\":\"The request is invalid\"}");

    }

    @Test
    public void serviceUnavailable_returns_code503() {
        assertThat(Answer.serviceUnavailable().getCode()).isEqualTo(503);
        assertThat(Answer.serviceUnavailable().getBody()).isEqualTo("{\"message\":\"Genome reporter tool is down\"}");
    }
}
