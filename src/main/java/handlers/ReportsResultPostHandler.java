package handlers;

import dataaccess.DataAccess;
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
    protected Answer processRequest(ReportRequestResult payload, Map<String, String> requestParams) {
        return Answer.notFound();
    }
}
