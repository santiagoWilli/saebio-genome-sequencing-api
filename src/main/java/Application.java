import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        post("/sequencies", (request, response) -> {
            response.status(201);
            return "";
        });
    }
}
