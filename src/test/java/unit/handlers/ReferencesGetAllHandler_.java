package unit.handlers;

import dataaccess.DataAccess;
import handlers.ReferencesGetAllHandler;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReferencesGetAllHandler_ {
    @Test
    public void alwaysReturnHttp200_and_callDataAccessMethodOnce() {
        final DataAccess dataAccess = mock(DataAccess.class);
        when(dataAccess.getAllReferences()).thenReturn("abc");
        assertThat(new ReferencesGetAllHandler(dataAccess).process(new EmptyPayload(), null))
                .isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getAllReferences();
    }
}
