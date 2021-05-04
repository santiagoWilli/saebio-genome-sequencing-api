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
        when(dataAccess.uploadTrimmedFile(trimResult)).thenReturn(UploadCode.NOT_FOUND);
        assertThat(handler.process(trimResult, null)).isEqualTo(new Answer(404, notFoundJson()));
    }

    @Test
    public void if_trimmedSequenceIsSuccessfullyUploaded_return_httpOk() {
        when(dataAccess.uploadTrimmedFile(trimResult)).thenReturn(UploadCode.OK);
        assertThat(handler.process(trimResult, null)).isEqualTo(new Answer(200, okJson()));
    }

    @Test
    public void if_writeExceptionWhenUploadingTheTrimmedSequence_return_httpServerError() {
        when(dataAccess.uploadTrimmedFile(trimResult)).thenReturn(UploadCode.WRITE_FAILED);
        assertThat(handler.process(trimResult, null)).isEqualTo(new Answer(500, errorJson()));
    }

    private String notFoundJson() {
        return "{\"message\":\"Could not find the sequence with the given token\"}";
    }

    private String okJson() {
        return "{\"message\":\"Trimmed sequence uploaded\"}";
    }

    private String errorJson() {
        return "{\"message\":\"The upload encountered a fatal error\"}";
    }
}
