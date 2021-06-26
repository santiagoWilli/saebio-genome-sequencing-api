package handlers.sequences;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.TrimRequestResult;
import utils.Answer;
import utils.RequestParams;

public class TrimmedSequencesPostHandler extends AbstractHandler<TrimRequestResult> {
    private final DataAccess dataAccess;

    public TrimmedSequencesPostHandler(DataAccess dataAccess) {
        super(TrimRequestResult.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(TrimRequestResult result, RequestParams requestParams) {
        if (result.getStatusCode() == 5) {
            if (dataAccess.setSequenceTrimToFalse(result.getToken())) {
                return new Answer(200, "Sequence trimmed files field updated to false");
            }
            return new Answer(404, "Could not find the sequence with the given token");
        }
        switch (dataAccess.uploadTrimmedFiles(result)) {
            case OK:
                return Answer.withMessage(200, "Trimmed sequence uploaded");
            case NOT_FOUND:
                return Answer.notFound();
            default:
                return Answer.serverError("The upload encountered a fatal error");
        }
    }
}
