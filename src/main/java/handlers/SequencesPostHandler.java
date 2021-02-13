package handlers;

import dataaccess.DataAccess;
import genome.GenomeTool;
import payloads.Sequence;
import utils.Answer;

public class SequencesPostHandler extends AbstractHandler<Sequence> {
    private final GenomeTool genomeTool;
    private final DataAccess dataAccess;

    public SequencesPostHandler(GenomeTool genomeTool, DataAccess dataAccess) {
        this.genomeTool = genomeTool;
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Sequence sequence) {
        Answer toolAnswer = genomeTool.requestTrim(sequence);
        if (toolAnswer.getCode() == 404 || toolAnswer.getCode() == 500) {
            return toolAnswer.getCode() == 404 ?
                    Answer.serviceUnavailable("Genome reporter tool is down") :
                    Answer.badGateway("Genome reporter tool encountered an internal error");
        }
        return new Answer(202, jsonOf(dataAccess.createSequence(sequence)));
    }

    private static String jsonOf(String id) {
        return "{\"id\":\"" + id + "\"}";
    }
}
