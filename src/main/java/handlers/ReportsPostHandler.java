package handlers;

import dataaccess.DataAccess;
import genome.GenomeTool;
import genome.GenomeToolAnswer;
import payloads.ReportRequest;
import utils.Answer;
import utils.Json;

import java.util.Map;

public class ReportsPostHandler extends AbstractHandler<ReportRequest> {
    private final GenomeTool genomeTool;
    private final DataAccess dataAccess;

    public ReportsPostHandler(GenomeTool genomeTool, DataAccess dataAccess) {
        super(ReportRequest.class);
        this.genomeTool = genomeTool;
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(ReportRequest reportRequest, Map<String, String> requestParams) {
        try {
            if (!dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences()))
                return new Answer(409, "Reference and sequences must share the same strain");
        } catch (NullPointerException e) {
            return new Answer(404, "One or more of the requested elements for the report could not be found");
        }
        GenomeToolAnswer toolAnswer = genomeTool.requestAnalysis(reportRequest);
        switch (toolAnswer.getStatus()) {
            case OK:
                return new Answer(202, Json.id(dataAccess.createReport(reportRequest, toolAnswer.getMessage())));
            case API_DOWN:
                return Answer.serviceUnavailable("Genome reporter tool is down");
            case SERVER_ERROR:
                return Answer.badGateway("Genome reporter tool encountered an internal error");
            case EXCEPTION_ENCOUNTERED:
                return Answer.serverError(toolAnswer.getMessage());
            default:
                throw new IllegalArgumentException();
        }
    }
}
