package handlers;

import dataaccess.DataAccess;
import payloads.EmptyPayload;
import utils.Answer;

public class SequencesGetHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public SequencesGetHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload) {
        return new Answer(200, dataAccess.getAllSequences());
    }
}