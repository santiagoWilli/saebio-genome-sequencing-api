package unit.handlers;

import dataaccess.DataAccess;
import dataaccess.exceptions.UniquenessViolationException;
import handlers.StrainsPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Strain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrainsPostHandler_ {
    private StrainsPostHandler handler;
    private DataAccess dataAccess;
    private Strain strain;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        strain = mock(Strain.class);
        when(strain.isValid()).thenReturn(true);
        handler = new StrainsPostHandler(dataAccess);
    }

    @Test
    public void if_nameOrKeyAlreadyExists_return_badRequest() throws UniquenessViolationException {
        when(dataAccess.createStrain(strain)).thenThrow(UniquenessViolationException.class);
        assertThat(handler.process(strain, null).getCode()).isEqualTo(409);
    }

    @Test
    public void if_strainCreated_return_httpOk() throws UniquenessViolationException {
        when(dataAccess.createStrain(strain)).thenReturn("abc");
        assertThat(handler.process(strain, null).getCode()).isEqualTo(200);
    }
}
