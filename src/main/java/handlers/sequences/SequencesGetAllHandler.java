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
        if (requestParams.query().get("strain") != null) {
            return new Answer(200, dataAccess.getAllSequences(requestParams.query().get("strain")[0]));
        }
        if (requestParams.query().get("month") == null || requestParams.query().get("year") == null) {
            return Answer.badRequest("You must specify month and year");
        }
        return new Answer(200, requestParams.query().get("field") == null ?
                dataAccess.getAllSequencesBySequenceDate(requestParams.query().get("year")[0], requestParams.query().get("month")[0]) :
                dataAccess.getAllSequencesByUploadDate(requestParams.query().get("year")[0], requestParams.query().get("month")[0]));
    }
}