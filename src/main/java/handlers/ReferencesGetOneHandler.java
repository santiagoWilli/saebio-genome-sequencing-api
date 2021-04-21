package handlers;

import dataaccess.DataAccess;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.Map;

public class ReferencesGetOneHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public ReferencesGetOneHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, Map<String, String> requestParams) {
        return Answer.notFound();
    }
}