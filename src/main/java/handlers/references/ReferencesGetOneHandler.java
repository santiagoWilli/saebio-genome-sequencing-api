package handlers.references;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReferencesGetOneHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public ReferencesGetOneHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, Map<String, String> requestParams) {
        String referenceJson = dataAccess.getReference(requestParams.get(":id"));
        if (referenceJson.isEmpty()) return Answer.notFound();

        Map<String, Object> reference;
        try {
            reference = new ObjectMapper().readValue(referenceJson, new TypeReference<HashMap<String,Object>>(){});
            Map<String, String> file = (Map<String, String>) reference.get("file");
            InputStream fileStream = dataAccess.getFileStream(file.get("$oid"));

            return Answer.withFile(fileStream, "text/x-fasta");
        } catch (IOException e) {
            return Answer.serverError(e.getMessage());
        }
    }
}