import com.beust.jcommander.JCommander;
import static spark.Spark.*;

import dataaccess.Database;
import dataaccess.MongoDataAccess;
import genome.NullarborClient;
import handlers.*;
import utils.Arguments;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Application {
    private static JCommander jCommander;

    public static void main(String[] args) {
        Arguments options = new Arguments();
        jCommander = JCommander.newBuilder()
                .addObject(options)
                .build();
        jCommander.parse(args);

        if (options.help) printHelp();
        if (options.genomeToolUrl == null) abnormalExit("Debes proporcionar el dominio de la herramienta de secuenciación del genoma.");
        try {
            new URL(options.genomeToolUrl).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            abnormalExit("El dominio de la herramienta de secuenciación proporcionado no es válido.");
        }

        port(options.port);
        Database.setPort(options.dbPort);
        Database.setDatabaseName(options.database);
        Database.setHost(options.dbHost);

        path("/api", () -> {
            get("/alive", (request, response) -> "I am alive!");

            path("/sequences", () -> {
                get("", new SequencesGetAllHandler(new MongoDataAccess()));
                get("/:id", new SequencesGetOneHandler(new MongoDataAccess()));
                get("/:id/trimmed", new SequencesGetTrimmedPairHandler(new MongoDataAccess()));
                post("", new SequencesPostHandler(new NullarborClient(options.genomeToolUrl), new MongoDataAccess()));
                post("/trimmed", (new TrimmedSequencesPostHandler(new MongoDataAccess())));
            });

            path("/references", () -> {
                get("", new ReferencesGetAllHandler(new MongoDataAccess()));
                get("/:id", new ReferencesGetOneHandler(new MongoDataAccess()));
                post("", new ReferencesPostHandler(new MongoDataAccess()));
            });

            path("/strains", () -> {
                get("", new StrainsGetAllHandler(new MongoDataAccess()));
                post("", new StrainsPostHandler(new MongoDataAccess()));
            });
        });

        exception(Exception.class, (exception, request, response) -> System.out.println(" -ERROR: " + exception.getMessage()));
    }

    private static void printHelp() {
        jCommander.usage();
        stop();
        System.exit(0);
    }

    private static void abnormalExit(String message) {
        System.out.println(message + "\n" + " --help para obtener ayuda.");
        stop();
        System.exit(1);
    }
}