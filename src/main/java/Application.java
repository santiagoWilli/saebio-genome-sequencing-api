import javax.servlet.MultipartConfigElement;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        post("/sequencies", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try {
                if (request.raw().getParts().size() != 2) throw new Exception();
            } catch (Exception e) {
                response.status(400);
                return "";
            }

            response.status(201);
            return "";
        });
    }
}
