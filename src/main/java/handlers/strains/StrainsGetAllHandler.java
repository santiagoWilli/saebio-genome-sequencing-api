package handlers.strains;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

public class StrainsGetAllHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public StrainsGetAllHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        return new Answer(200, dataAccess.getAllStrains());
    }
}