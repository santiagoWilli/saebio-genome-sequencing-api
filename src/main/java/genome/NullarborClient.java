package genome;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import payloads.Sequence;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

public class NullarborClient implements GenomeTool {
    private final String endpoint;

    public NullarborClient(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public GenomeToolAnswer requestTrim(Sequence sequence) {
        Part[] parts = new Part[2]; int i = 0;
        for (Part part : sequence.getFileParts()) parts[i++] = part;

        try (InputStream inputStream1 = parts[0].getInputStream(); InputStream inputStream2 = parts[1].getInputStream()) {
            HttpResponse<JsonNode> response = Unirest.post(endpoint + "/trim")
                    .field("pair1", inputStream1, parts[0].getSubmittedFileName())
                    .field("pair2", inputStream2, parts[1].getSubmittedFileName())
                    .asJson();
            if (response.getStatus() == 202) {
                return new GenomeToolAnswer(
                        GenomeToolAnswer.Status.OK,
                        response.getBody().getObject().get("token").toString());
            }
            return new GenomeToolAnswer(response.getStatus() == 404 ? GenomeToolAnswer.Status.API_DOWN : GenomeToolAnswer.Status.SERVER_ERROR);
        } catch (IOException e) {
            return new GenomeToolAnswer(GenomeToolAnswer.Status.EXCEPTION_ENCOUNTERED);
        }
    }
}
