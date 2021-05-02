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
        return genomeTool.requestAnalysis(reportRequest).getStatus() == GenomeToolAnswer.Status.API_DOWN ? Answer.serviceUnavailable("Genome reporter tool is down") : Answer.badGateway("Genome reporter tool encountered an internal error");
    }
}
