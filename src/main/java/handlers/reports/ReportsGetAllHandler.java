package handlers.reports;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

public class ReportsGetAllHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public ReportsGetAllHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        if (requestParams.query().get("month") == null || requestParams.query().get("year") == null) {
            return Answer.badRequest("You must specify month and year");
        }
        return new Answer(200, dataAccess.getAllReports(requestParams.query().get("year")[0], requestParams.query().get("month")[0]));
    }
}