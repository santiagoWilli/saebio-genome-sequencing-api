import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        post("/sequencies", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try {
                Part filePart = request.raw().getPart("pair1");
            } catch (Exception e) {
                response.status(400);
                return "";
            }

            response.status(201);
            return "";
        });
    }
}
