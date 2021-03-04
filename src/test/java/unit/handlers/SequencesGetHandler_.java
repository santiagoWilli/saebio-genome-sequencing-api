package unit.handlers;

import dataaccess.DataAccess;
import handlers.SequencesGetHandler;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SequencesGetHandler_ {
    @Test
    public void alwaysReturnHttp200() {
        final DataAccess dataAccess = mock(DataAccess.class);
        when(dataAccess.getAllSequences()).thenReturn("abc");
        assertThat(new SequencesGetHandler(dataAccess).process(new EmptyPayload()))
                .isEqualTo(new Answer(200, "abc"));
    }
}
