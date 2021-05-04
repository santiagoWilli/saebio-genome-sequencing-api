package unit.handlers;

import dataaccess.DataAccess;
import handlers.sequences.SequencesGetOneHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class SequencesGetOneHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private SequencesGetOneHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new SequencesGetOneHandler(dataAccess);
    }

    @Test
    public void ifSequenceNotFound_returnHttpNotFound() {
        when(dataAccess.getSequence(PARAMS.get(":id"))).thenReturn("");
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(Answer.notFound());
    }

    @Test
    public void ifSequenceFound_returnHttpOk_and_sequenceJson() {
        when(dataAccess.getSequence(PARAMS.get(":id"))).thenReturn("abc");
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getSequence(PARAMS.get(":id"));
    }
}
