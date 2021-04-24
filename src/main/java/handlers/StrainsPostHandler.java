package handlers;

import dataaccess.DataAccess;
import payloads.Strain;
import utils.Answer;

import java.util.Map;

public class StrainsPostHandler extends AbstractHandler<Strain> {
    private final DataAccess dataAccess;

    public StrainsPostHandler(DataAccess dataAccess) {
        super(Strain.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(Strain strain, Map<String, String> requestParams) {
        return new Answer(400, "");
    }
}
