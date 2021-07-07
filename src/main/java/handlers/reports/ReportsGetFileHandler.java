package handlers.reports;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

import java.io.IOException;

public class ReportsGetFileHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public ReportsGetFileHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        if (dataAccess.getReport(requestParams.path().get(":id")).isEmpty()) return Answer.notFound();

        final String filename = requestParams.path().get(":file");
        String fileId, mimeType;
        if (filename.equals("report")) {
            fileId = dataAccess.getReportHTMLFileId(requestParams.path().get(":id"));
            mimeType = "text/html";
        } else if (filename.equals("log")) {
            fileId = dataAccess.getReportLogId(requestParams.path().get(":id"));
            mimeType = "text/plain";
        } else {
            fileId = dataAccess.getReportFileId(requestParams.path().get(":id"), filename);
            mimeType = filename.endsWith("csv") ? "text/csv" : "text/x-nh";
        }

        if (fileId == null) return Answer.notFound();

        try {
            return Answer.withFile(dataAccess.getFileStream(fileId), mimeType);
        } catch (IOException e) {
            return Answer.serverError(e.getMessage());
        }
    }
}