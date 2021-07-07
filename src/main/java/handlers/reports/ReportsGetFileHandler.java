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
        final String fileId = filename.equals("report") ?
                dataAccess.getReportHTMLFileId(requestParams.path().get(":id")) :
                dataAccess.getReportFileId(requestParams.path().get(":id"), filename);
        if (fileId == null) return Answer.notFound();

        try {
            final String mimeType = filename.equals("report") ?
                    "text/html" :
                    filename.endsWith("csv") ? "text/csv" : "text/x-nh";
            return Answer.withFile(dataAccess.getFileStream(fileId), mimeType);
        } catch (IOException e) {
            return Answer.serverError(e.getMessage());
        }
    }
}