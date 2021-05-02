package handlers;

import payloads.ReportRequest;
import utils.Answer;

import java.util.Map;

public class ReportsPostHandler extends AbstractHandler<ReportRequest> {
    public ReportsPostHandler() {
        super(ReportRequest.class);
    }

    @Override
    protected Answer processRequest(ReportRequest reportRequest, Map<String, String> requestParams) {
        return null;
    }
}
