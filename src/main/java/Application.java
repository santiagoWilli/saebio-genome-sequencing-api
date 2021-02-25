import com.beust.jcommander.JCommander;
import static spark.Spark.*;

import dataaccess.Database;
import dataaccess.MongoDataAccess;
import genome.NullarborClient;
import handlers.SequencesPostHandler;
import handlers.TrimmedSequencesPostHandler;
import utils.Arguments;

public class Application {
    public static void main(String[] args) {
        Arguments options = new Arguments();
        JCommander.newBuilder()
                .addObject(options)
                .build()
                .parse(args);
        port(options.port);
        Database.setPort(options.dbPort);
        Database.setDatabaseName(options.database);
        final String externalApiUri = options.database.contains("test") ?
                "http://localhost:7717" :
                "";

        get("/alive", (request, response) -> "I am alive!");

        post("/sequences", new SequencesPostHandler(new NullarborClient(externalApiUri), new MongoDataAccess()));

        post("/sequences/trimmed", (new TrimmedSequencesPostHandler(new MongoDataAccess())));
    }
}