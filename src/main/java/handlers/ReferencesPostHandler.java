package handlers;

import dataaccess.DataAccess;
import payloads.Reference;
import utils.Answer;
import utils.Json;

import java.io.IOException;
import java.util.Map;

public class ReferencesPostHandler extends AbstractHandler<Reference> {
    private final DataAccess dataAccess;

    public ReferencesPostHandler(DataAccess dataAccess) {
        super(Reference.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Reference reference, Map<String, String> requestParams) {
        if (dataAccess.getStrainName(reference.getStrainKey()) == null) {
            return Answer.badRequest(reference.getStrainKey() + " strain does not exist");
        }

        try {
            return new Answer(200, Json.id(dataAccess.uploadReference(reference)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Answer.serverError("Error encountered while uploading the given reference");
        }
    }
}
