package unit.handlers;

import dataaccess.DataAccess;
import handlers.reports.ReportsGetFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ReportsGetFileHandler_ {
    private ReportsGetFileHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new ReportsGetFileHandler(dataAccess);
    }

    @Test
    public void ifReportNotFound_returnHttpNotFound() {
        final Map<String, String> params = Map.ofEntries(new AbstractMap.SimpleEntry<>(":id", "1"));
        when(dataAccess.getReport(params.get(":id"))).thenReturn("");
        assertThat(handler.process(new EmptyPayload(), new RequestParams(params, null))).isEqualTo(Answer.notFound());
        verify(dataAccess, times(1)).getReport(params.get(":id"));
    }

    @Test
    public void ifReportFound_and_fileReportExists_returnHttpOk_and_HTMLFile() throws IOException {
        String fileId = "6075d6a71a62381d13c70a6f";
        final Map<String, String> params = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(":id", "1"),
                new AbstractMap.SimpleEntry<>(":file", "report"));
        when(dataAccess.getReport(params.get(":id"))).thenReturn("json");
        when(dataAccess.getReportHTMLFileId(params.get(":id"))).thenReturn(fileId);
        when(dataAccess.getFileStream(fileId)).thenReturn(new FileInputStream("test/resources/sequences/informe.html"));

        Answer answer = handler.process(new EmptyPayload(), new RequestParams(params, null));

        verify(dataAccess, times(1)).getReport(params.get(":id"));
        verify(dataAccess, times(1)).getReportHTMLFileId(params.get(":id"));
        verify(dataAccess, times(1)).getFileStream(fileId);
        verifyNoMoreInteractions(dataAccess);

        assertThat(answer.getCode()).isEqualTo(200);
        assertThat(answer.hasFile()).isTrue();
        assertThat(answer.getFile().getMimeType()).isEqualTo("text/html");
    }

    @Test
    public void ifReportFound_and_fileAskedForExists_returnHttpOk_and_file() throws IOException {
        String fileId = "6075d6a71a62381d13c70a6f";
        final Map<String, String> params = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(":id", "1"),
                new AbstractMap.SimpleEntry<>(":file", "resistomecsv"));
        when(dataAccess.getReport(params.get(":id"))).thenReturn("json");
        when(dataAccess.getReportFileId(params.get(":id"), params.get(":file"))).thenReturn(fileId);
        when(dataAccess.getFileStream(fileId)).thenReturn(new FileInputStream("test/resources/sequences/report/resistome.csv"));

        Answer answer = handler.process(new EmptyPayload(), new RequestParams(params, null));

        verify(dataAccess, times(1)).getReport(params.get(":id"));
        verify(dataAccess, times(1)).getReportFileId(params.get(":id"), params.get(":file"));
        verify(dataAccess, times(1)).getFileStream(fileId);
        verifyNoMoreInteractions(dataAccess);

        assertThat(answer.getCode()).isEqualTo(200);
        assertThat(answer.hasFile()).isTrue();
        assertThat(answer.getFile().getMimeType()).isEqualTo("text/csv");
    }

    @Test
    public void ifReportFound_and_HTMLFileDoesNotExists_returnHttpNotFound() {
        final Map<String, String> params = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(":id", "1"),
                new AbstractMap.SimpleEntry<>(":file", "report"));
        when(dataAccess.getReport(params.get(":id"))).thenReturn("json}");
        when(dataAccess.getReportHTMLFileId(params.get(":id"))).thenReturn(null);

        assertThat(handler.process(new EmptyPayload(), new RequestParams(params, null)).getCode()).isEqualTo(404);

        verify(dataAccess, times(1)).getReport(params.get(":id"));
        verify(dataAccess, times(1)).getReportHTMLFileId(params.get(":id"));
        verifyNoMoreInteractions(dataAccess);
    }

    @Test
    public void ifReportFound_and_fileDoesNotExists_returnHttpNotFound() {
        final Map<String, String> params = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(":id", "1"),
                new AbstractMap.SimpleEntry<>(":file", "nocsv"));
        when(dataAccess.getReport(params.get(":id"))).thenReturn("json}");
        when(dataAccess.getReportFileId(params.get(":id"), params.get(":file"))).thenReturn(null);

        assertThat(handler.process(new EmptyPayload(), new RequestParams(params, null)).getCode()).isEqualTo(404);

        verify(dataAccess, times(1)).getReport(params.get(":id"));
        verify(dataAccess, times(1)).getReportFileId(params.get(":id"), params.get(":file"));
        verifyNoMoreInteractions(dataAccess);
    }
}
