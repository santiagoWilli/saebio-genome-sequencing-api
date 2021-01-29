package utils;

import com.beust.jcommander.Parameter;

public class Arguments {
    @Parameter(names = {"-p", "--port"}, description = "Application port")
    public Integer port = 4567;

    @Parameter(names = {"-db", "--database"}, description = "Database name")
    public String database = "saebio";

    @Parameter(names = {"-dbp", "--db-port"}, description = "Database port")
    public Integer dbPort = 27017;
}
