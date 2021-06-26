package unit.handlers;

import dataaccess.DataAccess;
import handlers.reports.ReportsGetOneHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReportsGetOneHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private ReportsGetOneHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new ReportsGetOneHandler(dataAccess);
    }

    @Test
    public void ifReportNotFound_returnHttpNotFound() {
        when(dataAccess.getReport(PARAMS.get(":id"))).thenReturn("");
        assertThat(handler.process(new EmptyPayload(), new RequestParams(PARAMS, null))).isEqualTo(Answer.notFound());
        verify(dataAccess, times(1)).getReport(PARAMS.get(":id"));
    }

    @Test
    public void ifReportFound_returnHttpOk_and_reportJson() {
        when(dataAccess.getReport(PARAMS.get(":id"))).thenReturn("abc");
        assertThat(handler.process(new EmptyPayload(), new RequestParams(PARAMS, null))).isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getReport(PARAMS.get(":id"));
    }
}
