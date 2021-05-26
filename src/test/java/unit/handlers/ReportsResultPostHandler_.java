package unit.handlers;

import dataaccess.DataAccess;
import dataaccess.UploadCode;
import handlers.reports.ReportsResultPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.ReportRequestResult;
import utils.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReportsResultPostHandler_ {
    private ReportRequestResult reportResult;
    private ReportsResultPostHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        reportResult = mock(ReportRequestResult.class);
        when(reportResult.isValid()).thenReturn(true);
        dataAccess = mock(DataAccess.class);
        handler = new ReportsResultPostHandler(dataAccess);
        when(reportResult.getToken()).thenReturn("123e4567-e89b-12d3-a456-556642440000");
    }

    @Test
    public void if_successfulStatusCode_and_reportDoesNotExists_return_httpNotFound() {
        when(reportResult.getStatusCode()).thenReturn(2);
        when(dataAccess.uploadReportFiles(reportResult)).thenReturn(UploadCode.NOT_FOUND);
        assertThat(handler.process(reportResult, null)).isEqualTo(Answer.notFound());
        verify(dataAccess, times(1)).uploadReportFiles(reportResult);

    }

    @Test
    public void if_successfulStatusCode_and_reportFileIsSuccessfullyUploaded_return_httpOk() {
        when(reportResult.getStatusCode()).thenReturn(2);
        when(dataAccess.uploadReportFiles(reportResult)).thenReturn(UploadCode.OK);
        assertThat(handler.process(reportResult, null).getCode()).isEqualTo(200);
        verify(dataAccess, times(1)).uploadReportFiles(reportResult);
    }

    @Test
    public void if_successfulStatusCode_and_writeExceptionWhenUploadingTheReportFile_return_httpServerError() {
        when(reportResult.getStatusCode()).thenReturn(2);
        when(dataAccess.uploadReportFiles(reportResult)).thenReturn(UploadCode.WRITE_FAILED);
        assertThat(handler.process(reportResult, null).getCode()).isEqualTo(500);
        verify(dataAccess, times(1)).uploadReportFiles(reportResult);
    }

    @Test
    public void if_failureStatusCode_and_reportFound_httpOk() {
        when(reportResult.getStatusCode()).thenReturn(5);
        when(dataAccess.setReportFileToFalse(reportResult.getToken())).thenReturn(true);
        assertThat(handler.process(reportResult, null).getCode()).isEqualTo(200);
        verify(dataAccess, times(1)).setReportFileToFalse(reportResult.getToken());
    }

    @Test
    public void if_failureStatusCode_and_reportNotFound_httpNotFound() {
        when(reportResult.getStatusCode()).thenReturn(5);
        when(dataAccess.setReportFileToFalse(reportResult.getToken())).thenReturn(false);
        assertThat(handler.process(reportResult, null)).isEqualTo(Answer.notFound());
        verify(dataAccess, times(1)).setReportFileToFalse(reportResult.getToken());
    }
}
