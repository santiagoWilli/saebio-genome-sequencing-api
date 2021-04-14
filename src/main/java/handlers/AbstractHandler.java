package handlers;

import org.apache.commons.io.FileUtils;
import payloads.EmptyPayload;
import payloads.Validable;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.Answer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractHandler<V extends Validable> implements RequestHandler<V>, Route {
    private final Class<V> payloadClass;

    protected AbstractHandler(Class<V> payloadClass) {
        this.payloadClass = payloadClass;
    }

    @Override
    public final Answer process(V payload, Map<String, String> requestParams) {
        if (payload != null && !payload.isValid()) {
            return Answer.badRequest("Cuerpo de la petición no válido");
        } else {
            return processRequest(payload, requestParams);
        }
    }

    protected abstract Answer processRequest(V payload, Map<String, String> requestParams);

    @Override
    public Object handle(Request request, Response response) throws IOException {
        String uuid = null;
        try {
            V payload = null;
            if (payloadClass != EmptyPayload.class) {
                request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
                Map<String, String> fields = new HashMap<>();
                Map<String, File> files = new HashMap<>();

                uuid = UUID.randomUUID().toString();
                for (Part part : request.raw().getParts()) {
                    if (part.getName().matches("^file[1-2]$")) {
                        File file = new File("temp/" + uuid + "/" + part.getName());
                        FileUtils.copyInputStreamToFile(part.getInputStream(), file);
                        files.put(part.getSubmittedFileName(), file);
                    } else {
                        String value = new BufferedReader(
                                new InputStreamReader(part.getInputStream(), StandardCharsets.UTF_8))
                                .lines()
                                .collect(Collectors.joining("\n"));
                        fields.put(part.getName(), value);
                    }
                    part.delete();
                }
                payload = payloadClass.getConstructor(Map.class, Map.class).newInstance(fields, files);
            }

            Answer answer = process(payload, request.params());
            deleteTempFiles(uuid);

            response.status(answer.getCode());
            response.type("application/json");
            return answer.getBody();
        } catch (Exception e) {
            deleteTempFiles(uuid);
            response.status(500);
            response.type("application/json");
            return Answer.serverError(e.getMessage());
        }
    }

    private void deleteTempFiles(String uuid) throws IOException {
        if (payloadClass != EmptyPayload.class) FileUtils.deleteDirectory(new File("temp/" + uuid));
    }
}
