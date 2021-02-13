package unit;

import dataaccess.DataAccess;
import genome.GenomeTool;
import utils.Answer;
import handlers.SequencesPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SequencesPostHandler_ {
    private Sequence sequence;
    private GenomeTool genomeTool;
    private Answer toolAnswer;
    private SequencesPostHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        sequence = mock(Sequence.class);
        when(sequence.isValid()).thenReturn(true);
        genomeTool = mock(GenomeTool.class);
        toolAnswer = mock(Answer.class);
        dataAccess = mock(DataAccess.class);
        handler = new SequencesPostHandler(genomeTool, dataAccess);
    }

    @Test
    public void serviceUnavailable_if_genomeToolAnswerIsApiDown() {
        when(genomeTool.requestTrim(sequence)).thenReturn(toolAnswer);
        when(toolAnswer.getCode()).thenReturn(GenomeTool.Response.API_DOWN.code());
        assertThat(handler.process(sequence)).isEqualTo(Answer.serviceUnavailable("Genome reporter tool is down"));
    }

    @Test
    public void badGateway_if_genomeToolAnswerIsServerError() {
        when(genomeTool.requestTrim(sequence)).thenReturn(toolAnswer);
        when(toolAnswer.getCode()).thenReturn(GenomeTool.Response.SERVER_ERROR.code());
        assertThat(handler.process(sequence)).isEqualTo(Answer.badGateway("Genome reporter tool encountered an internal error"));
    }

    @Test
    public void accepted_if_genomeToolAnswerIsOk() {
        when(genomeTool.requestTrim(sequence)).thenReturn(toolAnswer);
        when(toolAnswer.getCode()).thenReturn(GenomeTool.Response.OK.code());
        String id = "507f1f77bcf86cd799439011";
        when(dataAccess.createSequence(sequence)).thenReturn(id);

        assertThat(handler.process(sequence)).isEqualTo(new Answer(202, acceptedBodyJson(id)));
    }

    private String acceptedBodyJson(String id) {
        return "{\"id\":\"" + id + "\"}";
    }
}
