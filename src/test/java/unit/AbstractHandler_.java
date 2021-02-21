package unit;

import handlers.AbstractHandler;
import utils.Answer;
import org.junit.jupiter.api.Test;
import payloads.Validable;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractHandler_ {
    @Test
    public void invalidPayload_returns_badRequest() {
        Validable payload = () -> false;
        AbstractHandler<Validable> handler = new AbstractHandler<>(Validable.class) {
            @Override
            protected Answer processRequest(Validable payload) {
                return null;
            }
        };
        assertThat(handler.process(payload)).isEqualTo(Answer.badRequest("Cuerpo de la petición no válido"));
    }
}
