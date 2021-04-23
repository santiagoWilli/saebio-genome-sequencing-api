package unit.handlers;

import dataaccess.DataAccess;
import handlers.StrainsGetAllHandler;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StrainsGetAllHandler_ {
    @Test
    public void alwaysReturnHttp200_and_callDataAccessMethodOnce() {
        final DataAccess dataAccess = mock(DataAccess.class);
        when(dataAccess.getAllStrains()).thenReturn("abc");
        assertThat(new StrainsGetAllHandler(dataAccess).process(new EmptyPayload(), null))
                .isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getAllStrains();
    }
}
