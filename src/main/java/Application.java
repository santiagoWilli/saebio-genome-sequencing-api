import javax.servlet.MultipartConfigElement;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        post("/sequencies", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try {
                if (request.raw().getParts().size() != 2) throw new Exception();
                String[] pair1Filename = request.raw().getPart("pair1").getSubmittedFileName().split("_");
                String[] pair2Filename = request.raw().getPart("pair2").getSubmittedFileName().split("_");
                if (!pair1Filename[0].equals(pair2Filename[0]) || !pair1Filename[1].equals(pair2Filename[1])) throw new Exception();
            } catch (Exception e) {
                response.status(400);
                return "";
            }

            response.status(201);
            return "";
        });
    }
}
