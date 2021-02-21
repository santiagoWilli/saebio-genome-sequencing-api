package handlers;

import dataaccess.DataAccess;
import genome.GenomeTool;
import genome.GenomeToolAnswer;
import payloads.Sequence;
import utils.Answer;

public class SequencesPostHandler extends AbstractHandler<Sequence> {
    private final GenomeTool genomeTool;
    private final DataAccess dataAccess;

    public SequencesPostHandler(GenomeTool genomeTool, DataAccess dataAccess) {
        super(Sequence.class);
        this.genomeTool = genomeTool;
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Sequence sequence) {
        GenomeToolAnswer toolAnswer = genomeTool.requestTrim(sequence);
        return switch (toolAnswer.getStatus()) {
            case OK -> new Answer(202, jsonOf(dataAccess.createSequence(sequence, toolAnswer.getMessage())));
            case API_DOWN -> Answer.serviceUnavailable("Genome reporter tool is down");
            case SERVER_ERROR -> Answer.badGateway("Genome reporter tool encountered an internal error");
            case EXCEPTION_ENCOUNTERED -> Answer.serverError(toolAnswer.getMessage());
        };
    }

    private static String jsonOf(String id) {
        return "{\"id\":\"" + id + "\"}";
    }
}
