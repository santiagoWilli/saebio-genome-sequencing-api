import spark.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import java.io.IOException;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        post("/sequencies", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try {
                if (request.raw().getParts().size() != 2) throw new Exception();
                if (!partFilesFormASequencyPair(request)) throw new Exception();
            } catch (Exception e) {
                response.status(400);
                return "";
            }

            response.status(201);
            return "";
        });
    }

    private static Boolean partFilesFormASequencyPair(Request request) throws IOException, ServletException {
        String[] r1Filename = getFilenameFieldsOfPart(request.raw().getPart("pair1"));
        String[] r2Filename = getFilenameFieldsOfPart(request.raw().getPart("pair2"));
        if (!r1Filename[0].equals(r2Filename[0]) || !r1Filename[1].equals(r2Filename[1])) return false;
        return !r1Filename[2].equals(r2Filename[2]);
    }

    private static String[] getFilenameFieldsOfPart(Part part) {
        String[] nameFields = part.getSubmittedFileName().split("_");
        nameFields[2] = nameFields[2].substring(0, nameFields[2].indexOf("."));
        return nameFields;
    }
}
