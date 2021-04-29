package unit.handlers;

import dataaccess.DataAccess;
import dataaccess.exceptions.UniquenessViolationException;
import handlers.StrainsPatchHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.StrainKeys;
import utils.Answer;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StrainsPatchHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private StrainsPatchHandler handler;
    private DataAccess dataAccess;
    private StrainKeys keys;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        keys = mock(StrainKeys.class);
        when(keys.isValid()).thenReturn(true);
        handler = new StrainsPatchHandler(dataAccess);
    }

    @Test
    public void ifDataAccessReturnsFalse_returnHttpNotFound() throws UniquenessViolationException {
        when(dataAccess.updateStrainKeys(PARAMS.get(":id"), keys)).thenReturn(false);
        assertThat(handler.process(keys, PARAMS)).isEqualTo(Answer.notFound());
        verify(dataAccess, times(1)).updateStrainKeys(PARAMS.get(":id"), keys);
    }

    @Test
    public void ifDataAccessReturnsTrue_returnHttpOk() throws UniquenessViolationException {
        when(dataAccess.updateStrainKeys(PARAMS.get(":id"), keys)).thenReturn(true);
        assertThat(handler.process(keys, PARAMS).getCode()).isEqualTo(200);
        verify(dataAccess, times(1)).updateStrainKeys(PARAMS.get(":id"), keys);
    }

    @Test
    public void ifDataAccessThrowsException_returnHttpConflict() throws UniquenessViolationException {
        when(dataAccess.updateStrainKeys(PARAMS.get(":id"), keys)).thenThrow(UniquenessViolationException.class);
        assertThat(handler.process(keys, PARAMS).getCode()).isEqualTo(409);
        verify(dataAccess, times(1)).updateStrainKeys(PARAMS.get(":id"), keys);
    }
}
