package handlers;

import dataaccess.DataAccess;
import genome.GenomeTool;
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
        dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences());
        return new Answer(409, "");
    }
}
