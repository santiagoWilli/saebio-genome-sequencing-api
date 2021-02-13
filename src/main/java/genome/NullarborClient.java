package genome;

import kong.unirest.Unirest;
import payloads.Sequence;
import utils.Answer;

import java.io.IOException;
import java.io.InputStream;

public class NullarborClient implements GenomeTool {
    private final String ENDPOINT;

    public NullarborClient() {
        ENDPOINT = "needstobechanged.tft.saebio.org";
    }

    public NullarborClient(String endpoint) {
        ENDPOINT = endpoint;
    }

    @Override
    public Answer requestTrim(Sequence sequence) {
        try (InputStream inputStream = sequence.getFileParts().iterator().next().getInputStream()) {
            Unirest.post(ENDPOINT + "/trim").body("").asEmpty();
            return new Answer(Response.API_DOWN.code());
        } catch (IOException e) {
            return new Answer(Response.EXCEPTION_ENCOUNTERED.code());
        }
    }
}
