package genome;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import payloads.Sequence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NullarborClient implements GenomeTool {
    private final String endpoint;

    public NullarborClient(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public GenomeToolAnswer requestTrim(Sequence sequence) {
        String[] fileNames = new String[2]; int i = 0;
        for (String fileName : sequence.getFiles().keySet()) fileNames[i++] = fileName;

        try (InputStream inputStream1 = new FileInputStream(sequence.getFiles().get(fileNames[0]));
             InputStream inputStream2 = new FileInputStream(sequence.getFiles().get(fileNames[1]))) {
            HttpResponse<JsonNode> response = Unirest.post(endpoint + "/trim")
                    .field("pair1", inputStream1, fileNames[0])
                    .field("pair2", inputStream2, fileNames[1])
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
