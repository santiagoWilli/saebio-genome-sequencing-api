package unit.handlers;

import dataaccess.DataAccess;
import handlers.ReferencesPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Reference;
import utils.Answer;
import utils.Json;

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
        when(reference.getStrainKey()).thenReturn("kp");
        dataAccess = mock(DataAccess.class);
        handler = new ReferencesPostHandler(dataAccess);
    }

    @Test
    public void if_writeExceptionWhenUploadingTheReference_return_httpServerError() throws IOException {
        when(dataAccess.getStrainName("kp")).thenReturn("klebsi");
        when(dataAccess.uploadReference(reference)).thenThrow(IOException.class);
        assertThat(handler.process(reference, null).getCode()).isEqualTo(500);
    }

    @Test
    public void if_referenceIsSuccessfullyUploaded_return_httpOk() throws IOException {
        when(dataAccess.getStrainName("kp")).thenReturn("klebsi");
        when(dataAccess.uploadReference(reference)).thenReturn("1234");
        assertThat(handler.process(reference, null)).isEqualTo(new Answer(200, Json.id("1234")));
    }


    @Test
    public void if_referenceStrainKeyDoesNotExist_return_httpBadRequest() {
        when(dataAccess.getStrainName("kp")).thenReturn(null);
        assertThat(handler.process(reference, null).getCode()).isEqualTo(400);
        verify(dataAccess, times(1)).getStrainName("kp");
        verifyNoMoreInteractions(dataAccess);
    }
}
