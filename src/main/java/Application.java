import com.beust.jcommander.JCommander;
import static spark.Spark.*;
import utils.Arguments;

public class Application {
    public static void main(String[] args) {
        Arguments options = new Arguments();
        JCommander.newBuilder()
                .addObject(options)
                .build()
                .parse(args);
        port(options.port);

        get("/alive", (req, res) -> "I am alive!");
    }
}