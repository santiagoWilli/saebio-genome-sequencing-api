package handlers.reports;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

public class ReportsGetOneHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public ReportsGetOneHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        String reportJson = dataAccess.getReport(requestParams.path().get(":id"));
        if (reportJson.isEmpty()) return Answer.notFound();
        return new Answer(200, reportJson);
    }
}