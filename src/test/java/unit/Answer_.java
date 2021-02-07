package unit;

import handlers.Answer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Answer_ {
    @Test
    public void badRequest_returns_code400() {
        String message = "The request is invalid";
        assertThat(Answer.badRequest(message).getCode()).isEqualTo(400);
        assertThat(Answer.badRequest(message).getBody()).isEqualTo("{\"message\":\"" + message + "\"}");

    }

    @Test
    public void serviceUnavailable_returns_code503() {
        String message = "Genome reporter tool is down";
        assertThat(Answer.serviceUnavailable(message).getCode()).isEqualTo(503);
        assertThat(Answer.serviceUnavailable(message).getBody()).isEqualTo("{\"message\":\"" + message + "\"}");
    }

    @Test
    public void badGateway_returns_code502() {
        String message = "Genome reporter tool encountered an internal error";
        assertThat(Answer.badGateway(message).getCode()).isEqualTo(502);
        assertThat(Answer.badGateway(message).getBody()).isEqualTo("{\"message\":\"" + message + "\"}");
    }
}
