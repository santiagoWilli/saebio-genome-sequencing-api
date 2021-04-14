package unit.handlers;

import dataaccess.DataAccess;
import handlers.SequencesGetTrimmedPairHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SequencesGetTrimmedPairHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("id", "1"));

    private SequencesGetTrimmedPairHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new SequencesGetTrimmedPairHandler(dataAccess);
    }

    @Test
    public void ifSequenceNotFound_returnHttpNotFound() {
        when(dataAccess.getSequence(PARAMS.get("id"))).thenReturn("");
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(Answer.notFound());
    }
}
