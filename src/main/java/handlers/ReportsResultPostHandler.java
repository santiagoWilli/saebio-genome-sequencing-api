package handlers;

import dataaccess.DataAccess;
import dataaccess.UploadCode;
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
        return dataAccess.uploadReportFile(result) == UploadCode.OK ?
                Answer.withMessage(200, "Report file uploaded") :
                Answer.notFound();
    }
}
