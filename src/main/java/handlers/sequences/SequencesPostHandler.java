package handlers.sequences;

import dataaccess.DataAccess;
import genome.GenomeTool;
import genome.GenomeToolAnswer;
import handlers.AbstractHandler;
import payloads.Sequence;
import utils.Answer;
import utils.Json;
import utils.RequestParams;

public class SequencesPostHandler extends AbstractHandler<Sequence> {
    private final GenomeTool genomeTool;
    private final DataAccess dataAccess;

    public SequencesPostHandler(GenomeTool genomeTool, DataAccess dataAccess) {
        super(Sequence.class);
        this.genomeTool = genomeTool;
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Sequence sequence, RequestParams requestParams) {
        if (!dataAccess.strainExists(sequence.getStrainKey())) {
            return Answer.badRequest(sequence.getStrainKey() + " strain does not exist");
        }

        if (dataAccess.sequenceAlreadyExists(sequence)) {
            return Answer.withMessage(409, "Sequence already exists");
        }

        if (sequence.isTrimmed()) {
            final String id = dataAccess.createSequenceAlreadyTrimmed(sequence);
            return id != null ?
                    new Answer(200, Json.id(id)) :
                    Answer.serverError("Error de escritura al subir los ficheros");
        }

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
