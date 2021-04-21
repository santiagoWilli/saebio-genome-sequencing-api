package unit.handlers;

import dataaccess.DataAccess;
import handlers.ReferencesPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Reference;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReferencesPostHandler_ {
    private Reference reference;
    private ReferencesPostHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        reference = mock(Reference.class);
        when(reference.isValid()).thenReturn(true);
        dataAccess = mock(DataAccess.class);
        handler = new ReferencesPostHandler(dataAccess);
    }

    @Test
    public void if_writeExceptionWhenUploadingTheReference_return_httpServerError() throws IOException {
        when(dataAccess.uploadReference(reference)).thenThrow(IOException.class);
        assertThat(handler.process(reference, null).getCode()).isEqualTo(500);
    }
}
