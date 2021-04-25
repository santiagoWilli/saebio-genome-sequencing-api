package unit.handlers;

import dataaccess.DataAccess;
import handlers.StrainsDeleteHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrainsDeleteHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private StrainsDeleteHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new StrainsDeleteHandler(dataAccess);
    }

    @Test
    public void ifStrainNotFound_returnHttpNotFound() {
        when(dataAccess.deleteStrain(PARAMS.get(":id"))).thenReturn(false);
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(Answer.notFound());
    }
}
