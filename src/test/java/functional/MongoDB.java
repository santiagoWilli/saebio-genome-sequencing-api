package functional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

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

    @Override
    public Map<String, Object> get(String collectionName, String field, String value) throws IOException {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document doc = collection.find(eq(field, value)).first();

        return doc == null ? null : new ObjectMapper().readValue(doc.toJson(), new TypeReference<HashMap<String,Object>>(){});
    }

    @Override
    public void insertFakeSequence(String token) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        collection.insertOne(new Document("genomeToolToken", token));
    }

    @Override
    public void insertFakeSequenceWithTrimmedFiles(String token, Collection<File> files) throws FileNotFoundException {
        MongoCollection<Document> collection = database.getCollection("sequences");
        Document document = new Document()
                .append("genomeToolToken", token);
        collection.insertOne(document);

        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        List<ObjectId> trimmedIds = new ArrayList<>();

        for (File file : files) {
            ObjectId id = gridFSBucket.uploadFromStream(file.getName(), new FileInputStream(file));
            trimmedIds.add(id);
        }
        collection.updateOne(eq("genomeToolToken", token), set("trimmedPair", trimmedIds));
    }

    @Override
    public void empty(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.deleteMany(new Document());
    }

    @Override
    public boolean referenceExists(String id) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSFile file = gridFSBucket.find(eq("_id", new ObjectId(id))).first();
        return file != null;
    }

    @Override
    public void insertFakeReference() {
        MongoCollection<Document> collection = database.getCollection("references");
        collection.insertOne(new Document());
    }

    @Override
    public String insertFakeReferenceWithFile(File file) throws FileNotFoundException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        ObjectId id = gridFSBucket.uploadFromStream(file.getName(), new FileInputStream(file));

        MongoCollection<Document> collection = database.getCollection("references");
        Document document = new Document("file", id);
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public void insertFakeStrain(String key) {
        MongoCollection<Document> collection = database.getCollection("strains");
        Document document = new Document()
                .append("keys", Collections.singletonList(key))
                .append("name", "anyName");
        collection.insertOne(document);
    }

    @Override
    public void insertFakeStrain(String key, String name) {
        MongoCollection<Document> collection = database.getCollection("strains");
        Document document = new Document()
                .append("keys", Collections.singletonList(key))
                .append("name", name);
        collection.insertOne(document);
    }

    @Override
    public boolean strainExists(String key) {
        MongoCollection<Document> collection = database.getCollection("strains");
        return collection.find(eq("keys", key)).first() != null;
    }
}
