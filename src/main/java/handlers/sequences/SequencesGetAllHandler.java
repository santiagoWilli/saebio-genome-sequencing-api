package handlers.sequences;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

public class SequencesGetAllHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public SequencesGetAllHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        if (requestParams.query().get("strain") == null) return new Answer(200, dataAccess.getAllSequences());
        return new Answer(200, dataAccess.getAllSequences(requestParams.query().get("strain")[0]));
    }
}