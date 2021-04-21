package handlers;

import dataaccess.DataAccess;
import genome.GenomeTool;
import genome.GenomeToolAnswer;
import payloads.Sequence;
import utils.Answer;
import utils.Json;

import java.util.Map;

public class SequencesPostHandler extends AbstractHandler<Sequence> {
    private final GenomeTool genomeTool;
    private final DataAccess dataAccess;

    public SequencesPostHandler(GenomeTool genomeTool, DataAccess dataAccess) {
        super(Sequence.class);
        this.genomeTool = genomeTool;
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Sequence sequence, Map<String, String> requestParams) {
        GenomeToolAnswer toolAnswer = genomeTool.requestTrim(sequence);
        switch (toolAnswer.getStatus()) {
            case OK:
                return new Answer(202, Json.id(dataAccess.createSequence(sequence, toolAnswer.getMessage())));
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
