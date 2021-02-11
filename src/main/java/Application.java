import com.beust.jcommander.JCommander;
import static spark.Spark.*;

import utils.Answer;
import utils.Arguments;

public class Application {
    public static void main(String[] args) {
        Arguments options = new Arguments();
        JCommander.newBuilder()
                .addObject(options)
                .build()
                .parse(args);
        port(options.port);

        get("/alive", (request, response) -> "I am alive!");

        post("/sequences", (request, response) -> {
            Answer answer = Answer.badRequest("La secuencia debe ser una pareja de ficheros");
            response.status(answer.getCode());
            response.type("application/json");
            return answer.getBody();
        });
    }
}