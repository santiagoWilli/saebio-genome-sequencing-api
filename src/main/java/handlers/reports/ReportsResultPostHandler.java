package handlers.reports;

import dataaccess.DataAccess;
import dataaccess.UploadCode;
import handlers.AbstractHandler;
import payloads.ReportRequestResult;
import utils.Answer;
import utils.RequestParams;

import java.io.IOException;

public class ReportsResultPostHandler extends AbstractHandler<ReportRequestResult> {
    private final DataAccess dataAccess;

    public ReportsResultPostHandler(DataAccess dataAccess) {
        super(ReportRequestResult.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(ReportRequestResult result, RequestParams requestParams) {
        if (result.getStatusCode() == 5) {
            if (dataAccess.setReportFileToFalse(result)) {
                return Answer.withMessage(200, "Report file updated to false");
            }
            return Answer.notFound();
        }
        UploadCode code;
        try {
            code = dataAccess.uploadReportFiles(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Answer.serverError("The upload encountered a fatal error");
        }
        switch (code) {
            case OK:
                return Answer.withMessage(200, "Report file uploaded");
            case NOT_FOUND:
                return Answer.notFound();
            default:
                return Answer.serverError("The upload encountered a fatal error");
        }
    }
}
