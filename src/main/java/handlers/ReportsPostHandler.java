package handlers;

import dataaccess.DataAccess;
import genome.GenomeTool;
import genome.GenomeToolAnswer;
import payloads.ReportRequest;
import utils.Answer;

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
        if (!dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences())) return new Answer(409, "");

        GenomeToolAnswer toolAnswer = genomeTool.requestAnalysis(reportRequest);
        switch (toolAnswer.getStatus()) {
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
