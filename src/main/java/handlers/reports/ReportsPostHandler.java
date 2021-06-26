package handlers.reports;

import dataaccess.DataAccess;
import genome.GenomeTool;
import genome.GenomeToolAnswer;
import handlers.AbstractHandler;
import payloads.ReportRequest;
import utils.Answer;
import utils.Json;
import utils.RequestParams;

import java.io.IOException;

public class ReportsPostHandler extends AbstractHandler<ReportRequest> {
    private final GenomeTool genomeTool;
    private final DataAccess dataAccess;

    public ReportsPostHandler(GenomeTool genomeTool, DataAccess dataAccess) {
        super(ReportRequest.class);
        this.genomeTool = genomeTool;
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(ReportRequest reportRequest, RequestParams requestParams) {
        try {
            if (!dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences()))
                return new Answer(409, "Reference and sequences must share the same strain");
        } catch (NullPointerException e) {
            return new Answer(404, "One or more of the requested elements for the report could not be found");
        }

        GenomeToolAnswer toolAnswer = genomeTool.requestToSendAnalysisFiles();
        if (toolAnswer.getStatus() != GenomeToolAnswer.Status.OK) return toolAnswerToAnswer(toolAnswer);
        String token = toolAnswer.getMessage();

        try {
            String fileId = dataAccess.getReferenceFileId(reportRequest.getReference());
            toolAnswer = genomeTool.sendAnalysisFile(token, dataAccess.getFileStream(fileId), dataAccess.getFileName(fileId));
        } catch (IOException e) {
            e.printStackTrace();
            return Answer.serverError(e.getMessage());
        }

        if (toolAnswer.getStatus() != GenomeToolAnswer.Status.OK) return toolAnswerToAnswer(toolAnswer);

        for (String sequenceId : reportRequest.getSequences()) {
            for (String fileId : dataAccess.getSequenceTrimmedFilesIds(sequenceId)) {
                try {
                    toolAnswer = genomeTool.sendAnalysisFile(token, dataAccess.getFileStream(fileId), dataAccess.getFileName(fileId));
                    if (toolAnswer.getStatus() != GenomeToolAnswer.Status.OK) return toolAnswerToAnswer(toolAnswer);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Answer.serverError(e.getMessage());
                }
            }
        }

        toolAnswer = genomeTool.requestToStartAnalysis(token);
        if (toolAnswer.getStatus() != GenomeToolAnswer.Status.OK) return toolAnswerToAnswer(toolAnswer);
        return new Answer(202, Json.id(dataAccess.createReport(reportRequest, token)));
    }

    private Answer toolAnswerToAnswer(GenomeToolAnswer toolAnswer) {
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
