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
        String reportJson = dataAccess.getReport(requestParams.get(":id"));
        if (reportJson.isEmpty()) return Answer.notFound();

        Map<String, Object> report;
        try {
            report = new ObjectMapper().readValue(reportJson, new TypeReference<HashMap<String,Object>>(){});
            Map<String, String> file = (Map<String, String>) report.get("file");
            InputStream fileStream = dataAccess.getFileStream(file.get("$oid"));

            return Answer.withFile(fileStream, "text/html");
        } catch (IOException e) {
            return Answer.serverError(e.getMessage());
        }
    }
}