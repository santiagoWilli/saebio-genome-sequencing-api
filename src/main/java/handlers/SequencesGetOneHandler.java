package handlers;

import dataaccess.DataAccess;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.Map;

public class SequencesGetOneHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public SequencesGetOneHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, Map<String, String> requestParams) {
        String sequenceJson = dataAccess.getSequence(requestParams.get(":id"));
        if (sequenceJson.isEmpty()) return Answer.notFound();
        return new Answer(200, sequenceJson);
    }
}