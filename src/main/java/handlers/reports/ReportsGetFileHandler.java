package handlers.reports;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportsGetFileHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public ReportsGetFileHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, Map<String, String> requestParams) {
        if (dataAccess.getReport(requestParams.get(":id")).isEmpty()) return Answer.notFound();

        final String fileId = dataAccess.getReportFileId(requestParams.get(":id"));
        try {
            InputStream fileStream = dataAccess.getFileStream(fileId);
            return Answer.withFile(fileStream, "text/html");
        } catch (IOException e) {
            return Answer.serverError(e.getMessage());
        }
    }
}