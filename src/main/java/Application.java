import com.beust.jcommander.JCommander;
import static spark.Spark.*;

import dataaccess.Database;
import dataaccess.MongoDataAccess;
import genome.NullarborClient;
import handlers.SequencesPostHandler;
import handlers.TrimmedSequencesPostHandler;
import utils.Arguments;

public class Application {
    private static JCommander jCommander;
    public static void main(String[] args) {
        Arguments options = new Arguments();
        jCommander = JCommander.newBuilder()
                .addObject(options)
                .build();
        jCommander.parse(args);

        if (options.help) printHelp();
        if (options.genomeToolUrl == null) abnormalExit();

        port(options.port);
        Database.setPort(options.dbPort);
        Database.setDatabaseName(options.database);

        get("/alive", (request, response) -> "I am alive!");

        path("/sequences", () -> {
            get("", (request, response) -> {
                response.status(501);
                return "";
            });
            post("", new SequencesPostHandler(new NullarborClient(options.genomeToolUrl), new MongoDataAccess()));
            post("/trimmed", (new TrimmedSequencesPostHandler(new MongoDataAccess())));
        });
    }

    private static void printHelp() {
        jCommander.usage();
        stop();
        System.exit(0);
    }

    private static void abnormalExit() {
        System.out.println("Debes proporcionar el dominio de la herramienta de secuenciación del genoma.\n" +
                " --help para obtener ayuda.");
        stop();
        System.exit(1);
    }
}