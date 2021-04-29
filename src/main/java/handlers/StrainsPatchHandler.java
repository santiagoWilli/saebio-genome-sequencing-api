package handlers;

import dataaccess.DataAccess;
import payloads.StrainKeys;
import utils.Answer;

import java.util.Map;

public class StrainsPatchHandler extends AbstractHandler<StrainKeys> {
    private final DataAccess dataAccess;

    public StrainsPatchHandler(DataAccess dataAccess) {
        super(StrainKeys.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(StrainKeys keys, Map<String, String> requestParams) {
        return Answer.notFound();
    }
}
