package unit;

import genome.GenomeTool;
import handlers.Answer;
import handlers.SequencesPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SequencesPostHandler_ {
    private Sequence sequence;
    private GenomeTool genomeTool;
    private SequencesPostHandler handler;

    @BeforeEach
    public void setUp() {
        sequence = mock(Sequence.class);
        when(sequence.isValid()).thenReturn(true);
        genomeTool = mock(GenomeTool.class);
        handler = new SequencesPostHandler(genomeTool);
    }

    @Test
    public void serviceUnavailable_if_genomeToolApiIsDown() {
        when(genomeTool.trim()).thenReturn(404);
        assertThat(handler.process(sequence)).isEqualTo(Answer.serviceUnavailable("Genome reporter tool is down"));
    }

    @Test
    public void badGateway_if_genomeToolApiEncountersAnInternalError() {
        when(genomeTool.trim()).thenReturn(500);
        assertThat(handler.process(sequence)).isEqualTo(Answer.badGateway("Genome reporter tool encountered an internal error"));
    }
}
