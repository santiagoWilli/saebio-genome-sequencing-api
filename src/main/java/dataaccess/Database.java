package dataaccess;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.List;

public class Database {
    private static String host;
    private static String databaseName;
    private static int port;

    private static class ConnectionHolder {
        public static MongoClient client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(List.of(new ServerAddress(host, port))))
                        .build());
    }

    private Database() {}

    public static MongoDatabase get() {
        return ConnectionHolder.client.getDatabase(databaseName);
    }

    public static void setPort(int port) {
        Database.port = port;
    }

    public static void setDatabaseName(String databaseName) {
        Database.databaseName = databaseName;
    }

    public static void setHost(String hostName) {
        Database.host = hostName;
    }
}
