package handlers.strains;

import dataaccess.DataAccess;
import dataaccess.exceptions.DocumentPointsToStrainException;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

public class StrainsDeleteHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public StrainsDeleteHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        try {
            return dataAccess.deleteStrain(requestParams.path().get(":id")) ?
                    new Answer(200, "Strain deleted") :
                    Answer.notFound();
        } catch (DocumentPointsToStrainException e) {
            return new Answer(409, "There are documents pointing to this strain");
        }
    }
}
