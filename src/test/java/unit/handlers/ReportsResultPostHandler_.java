package unit.handlers;

import dataaccess.DataAccess;
import dataaccess.UploadCode;
import handlers.ReportsResultPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.ReportRequestResult;
import utils.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        when(reportResult.getSequenceToken()).thenReturn("123e4567-e89b-12d3-a456-556642440000");
    }

    @Test
    public void if_reportDoesNotExists_return_httpNotFound() {
        when(dataAccess.uploadReportFile(reportResult)).thenReturn(UploadCode.NOT_FOUND);
        assertThat(handler.process(reportResult, null)).isEqualTo(Answer.notFound());
    }

    @Test
    public void if_reportFileIsSuccessfullyUploaded_return_httpOk() {
        when(dataAccess.uploadReportFile(reportResult)).thenReturn(UploadCode.OK);
        assertThat(handler.process(reportResult, null).getCode()).isEqualTo(200);
    }
}
