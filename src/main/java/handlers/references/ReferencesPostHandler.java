package handlers.references;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.Reference;
import utils.Answer;
import utils.Json;
import utils.RequestParams;

import java.io.IOException;

public class ReferencesPostHandler extends AbstractHandler<Reference> {
    private final DataAccess dataAccess;

    public ReferencesPostHandler(DataAccess dataAccess) {
        super(Reference.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Reference reference, RequestParams requestParams) {
        if (!dataAccess.strainExists(reference.getStrainKey())) {
            return Answer.badRequest(reference.getStrainKey() + " strain does not exist");
        }

        if (dataAccess.referenceAlreadyExists(reference)) {
            return Answer.withMessage(409, "Reference already exists");
        }

        try {
            return new Answer(200, Json.id(dataAccess.uploadReference(reference)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Answer.serverError("Error encountered while uploading the given reference");
        }
    }
}
