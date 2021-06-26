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
        return new Answer(200, requestParams.query().get("strain") == null ?
                dataAccess.getAllSequences() :
                dataAccess.getAllSequences(requestParams.query().get("strain")[0]));
    }
}