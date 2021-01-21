import spark.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import java.io.IOException;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        post("/sequences", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try {
                if (request.raw().getParts().size() != 2) throw new BadRequestException("La secuencia debe ser una pareja de ficheros.");
                for (Part part : request.raw().getParts()) {
                    if (!part.getSubmittedFileName().matches(filenameRegex())) throw new BadRequestException("Nombre de archivo inválido.");
                }
                if (!partFilesFormASequencyPair(request)) throw new BadRequestException("Los ficheros no forman una pareja válida.");
            } catch (Exception e) {
                response.status(400);
                response.type("application/json");
                return e.getMessage();
            }

            response.status(201);
            return "";
        });
    }

    private static String filenameRegex() {
        return "[a-zA-Z]+[0-9]{0,4}_((0[1-9])|([1-2][1-9])|(3[0-1]))((0[1-9])|(1[0-2]))[0-9]{2}_R(1|2).(fq|fastq).gz";
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

    static class BadRequestException extends Exception {
        BadRequestException(String message) {
            super("{\"message\":\"" + message + "\"}");
        }
    }
}

