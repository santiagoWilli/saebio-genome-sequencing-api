package unit;

import genome.GenomeTool;
import handlers.Answer;
import handlers.SequencesPostHandler;
import org.junit.jupiter.api.Test;
import payloads.Sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SequencesPostHandler_ {
    @Test
    public void serviceUnavailable_if_nullarborAPIisDown() {
        Sequence sequence = mock(Sequence.class);
        when(sequence.isValid()).thenReturn(true);
        GenomeTool genomeTool = mock(GenomeTool.class);
        when(genomeTool.trim()).thenReturn(404);

        SequencesPostHandler handler = new SequencesPostHandler(genomeTool);
        assertThat(handler.process(sequence)).isEqualTo(Answer.serviceUnavailable());
    }
}
