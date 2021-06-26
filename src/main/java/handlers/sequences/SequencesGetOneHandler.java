package handlers.sequences;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

public class SequencesGetOneHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public SequencesGetOneHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        String sequenceJson = dataAccess.getSequence(requestParams.path().get(":id"));
        if (sequenceJson.isEmpty()) return Answer.notFound();
        return new Answer(200, sequenceJson);
    }
}