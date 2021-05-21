package handlers.reports;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.ReportRequestResult;
import utils.Answer;

import java.util.Map;

public class ReportsResultPostHandler extends AbstractHandler<ReportRequestResult> {
    private final DataAccess dataAccess;

    public ReportsResultPostHandler(DataAccess dataAccess) {
        super(ReportRequestResult.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(ReportRequestResult result, Map<String, String> requestParams) {
        if (result.getStatusCode() == 5) {
            if (dataAccess.setReportFileToFalse(result.getSequenceToken())) {
                return Answer.withMessage(200, "Report file updated to false");
            }
            return Answer.notFound();
        }
        switch (dataAccess.uploadReportFile(result)) {
            case OK:
                return Answer.withMessage(200, "Report file uploaded");
            case NOT_FOUND:
                return Answer.notFound();
            default:
                return Answer.serverError("The upload encountered a fatal error");
        }
    }
}
