package handlers;

import dataaccess.DataAccess;
import payloads.TrimRequestResult;
import utils.Answer;

public class TrimmedSequencesPostHandler extends AbstractHandler<TrimRequestResult> {
    private final DataAccess dataAccess;

    public TrimmedSequencesPostHandler(DataAccess dataAccess) {
        super(TrimRequestResult.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(TrimRequestResult result) {
        return new Answer(404, notFoundJson(result.getSequenceToken()));
    }

    private String notFoundJson(String token) {
        return "{\"message\":\"Could not find the sequence with token " + token + "\"}";
    }
}
