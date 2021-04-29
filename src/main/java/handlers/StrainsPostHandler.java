package handlers;

import dataaccess.DataAccess;
import dataaccess.exceptions.UniquenessViolationException;
import payloads.Strain;
import utils.Answer;
import utils.Json;

import java.util.Map;

public class StrainsPostHandler extends AbstractHandler<Strain> {
    private final DataAccess dataAccess;

    public StrainsPostHandler(DataAccess dataAccess) {
        super(Strain.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Strain strain, Map<String, String> requestParams) {
        try {
            return new Answer(200, Json.id(dataAccess.createStrain(strain)));
        } catch (UniquenessViolationException e) {
            return new Answer(409, e.getMessage());
        }
    }
}
