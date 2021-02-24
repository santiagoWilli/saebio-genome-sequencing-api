package unit;

import dataaccess.DataAccess;
import dataaccess.UploadCode;
import handlers.TrimmedSequencesPostHandler;
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
        assertThat(handler.process(trimResult)).isEqualTo(new Answer(404, notFoundJson(trimResult.getSequenceToken())));
    }

    @Test
    public void if_trimmedSequenceIsSuccessfullyUploaded_return_httpOk() {
        when(dataAccess.uploadTrimmedFile(trimResult)).thenReturn(UploadCode.OK);
        assertThat(handler.process(trimResult)).isEqualTo(new Answer(200, okJson()));
    }

    private String notFoundJson(String token) {
        return "{\"message\":\"Could not find the sequence with token " + token + "\"}";
    }

    private String okJson() {
        return "{\"message\":\"Trimmed sequence uploaded\"}";
    }
}
