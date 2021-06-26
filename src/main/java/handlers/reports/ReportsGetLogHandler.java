package handlers.reports;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

import java.io.IOException;
import java.io.InputStream;

public class ReportsGetLogHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public ReportsGetLogHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        if (dataAccess.getReport(requestParams.path().get(":id")).isEmpty()) return Answer.notFound();
        final String fileId = dataAccess.getReportLogId(requestParams.path().get(":id"));
        if (fileId == null) return Answer.notFound();
        try {
            InputStream fileStream = dataAccess.getFileStream(fileId);
            return Answer.withFile(fileStream, "text/plain");
        } catch (IOException e) {
            return Answer.serverError(e.getMessage());
        }
    }
}