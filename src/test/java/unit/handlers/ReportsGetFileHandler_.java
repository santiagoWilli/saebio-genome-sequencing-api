package unit.handlers;

import dataaccess.DataAccess;
import handlers.reports.ReportsGetFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ReportsGetFileHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private ReportsGetFileHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new ReportsGetFileHandler(dataAccess);
    }

    @Test
    public void ifReportNotFound_returnHttpNotFound() {
        when(dataAccess.getReport(PARAMS.get(":id"))).thenReturn("");
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(Answer.notFound());
        verify(dataAccess, times(1)).getReport(PARAMS.get(":id"));
    }
}
