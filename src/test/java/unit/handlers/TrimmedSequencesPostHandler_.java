package unit.handlers;

import dataaccess.DataAccess;
import dataaccess.UploadCode;
import handlers.sequences.TrimmedSequencesPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.TrimRequestResult;
import utils.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TrimmedSequencesPostHandler_ {
    private TrimRequestResult trimResult;
    private TrimmedSequencesPostHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        trimResult = mock(TrimRequestResult.class);
        when(trimResult.isValid()).thenReturn(true);
        dataAccess = mock(DataAccess.class);
        handler = new TrimmedSequencesPostHandler(dataAccess);
        when(trimResult.getSequenceToken()).thenReturn("123e4567-e89b-12d3-a456-556642440000");
    }

    @Test
    public void if_sequenceDoesNotExists_return_httpNotFound() {
        when(dataAccess.uploadTrimmedFiles(trimResult)).thenReturn(UploadCode.NOT_FOUND);
        assertThat(handler.process(trimResult, null)).isEqualTo(Answer.notFound());
    }

    @Test
    public void if_trimmedSequenceIsSuccessfullyUploaded_return_httpOk() {
        when(dataAccess.uploadTrimmedFiles(trimResult)).thenReturn(UploadCode.OK);
        assertThat(handler.process(trimResult, null).getCode()).isEqualTo(200);
    }

    @Test
    public void if_writeExceptionWhenUploadingTheTrimmedSequence_return_httpServerError() {
        when(dataAccess.uploadTrimmedFiles(trimResult)).thenReturn(UploadCode.WRITE_FAILED);
        assertThat(handler.process(trimResult, null).getCode()).isEqualTo(500);
    }
}
