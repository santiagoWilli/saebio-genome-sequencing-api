package functional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class MongoDB implements Database {
    private final MongoDatabase database;

    public MongoDB(int port) {
        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(List.of(new ServerAddress("localhost", port))))
                        .build());
        database = mongoClient.getDatabase("test");
    }

    @Override
    public Map<String, Object> get(String collectionName, String id) throws JsonProcessingException {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document doc = collection.find(eq("_id", new ObjectId(id))).first();

        return doc == null ? null : new ObjectMapper().readValue(doc.toJson(), new TypeReference<HashMap<String,Object>>(){});
    }
}
