package unit.handlers;

import dataaccess.DataAccess;
import handlers.StrainsGetOneHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StrainsGetOneHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private StrainsGetOneHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new StrainsGetOneHandler(dataAccess);
    }

    @Test
    public void ifSequenceNotFound_returnHttpNotFound() {
        when(dataAccess.getStrain(PARAMS.get(":id"))).thenReturn("");
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(Answer.notFound());
    }

    @Test
    public void ifSequenceFound_returnHttpOk_and_sequenceJson() {
        when(dataAccess.getStrain(PARAMS.get(":id"))).thenReturn("abc");
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getStrain(PARAMS.get(":id"));
    }
}
