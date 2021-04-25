package handlers;

import dataaccess.DataAccess;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.Map;

public class StrainsDeleteHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public StrainsDeleteHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, Map<String, String> requestParams) {
        return dataAccess.deleteStrain(requestParams.get(":id")) ?
                new Answer(200, "Strain deleted") :
                Answer.notFound();
    }
}
