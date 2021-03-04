package utils;

import com.beust.jcommander.Parameter;

public class Arguments {
    @Parameter(names = {"-p", "--port"}, description = "Application port", order = 1)
    public Integer port = 4567;

    @Parameter(names = {"-db", "--database"}, description = "Database name")
    public String database = "saebio";

    @Parameter(names = {"-dbp", "--db-port"}, description = "Database port")
    public Integer dbPort = 27017;

    @Parameter(names = {"-gt", "--genome-tool"}, description = "Genome tool endpoint", order = 0)
    public String genomeToolUrl;

    @Parameter(names = "--help", help = true)
    public boolean help;
}
