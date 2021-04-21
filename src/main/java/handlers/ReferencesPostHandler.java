package handlers;

import dataaccess.DataAccess;
import payloads.Reference;
import utils.Answer;

import java.util.Map;

public class ReferencesPostHandler extends AbstractHandler<Reference> {
    private final DataAccess dataAccess;

    public ReferencesPostHandler(DataAccess dataAccess) {
        super(Reference.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Reference sequence, Map<String, String> requestParams) {
        return new Answer(500, "Error encountered while uploading the given reference");
    }
}
