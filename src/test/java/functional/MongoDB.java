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
import utils.EncryptedPassword;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Override
    public Map<String, Object> get(String collectionName, String field, String value) throws IOException {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document doc = collection.find(eq(field, value)).first();

        return doc == null ? null : new ObjectMapper().readValue(doc.toJson(), new TypeReference<HashMap<String,Object>>(){});
    }

    @Override
    public void insertFakeSequence(String token) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        collection.insertOne(new Document()
                .append("genomeToolToken", token)
        );
    }

    @Override
    public void insertFakeSequence(String token, String strainId) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        collection.insertOne(new Document()
                .append("genomeToolToken", token)
                .append("strain", new ObjectId(strainId))
        );
    }

    @Override
    public void insertFakeSequenceWithSequenceDate(String token, String date) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        collection.insertOne(new Document()
                .append("genomeToolToken", token)
                .append("sequenceDate", formatDate(date, "yyyy-MM-dd"))
        );
    }

    @Override
    public void insertFakeSequenceWithUploadDate(String token, String date) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        collection.insertOne(new Document()
                .append("genomeToolToken", token)
                .append("uploadDate", formatDate(date, "yyyy-MM-dd HH:mm:ss.SSS"))
        );
    }

    @Override
    public void insertFakeRepeatedSequence(String strainId, String code, String date) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        collection.insertOne(new Document()
                .append("strain", new ObjectId(strainId))
                .append("code", code)
                .append("sequenceDate", date)
        );
    }

    @Override
    public void insertFakeRepeatedReference(String strainId, String code) {
        MongoCollection<Document> collection = database.getCollection("references");
        collection.insertOne(new Document()
                .append("strain", new ObjectId(strainId))
                .append("code", code)
        );
    }

    @Override
    public String insertFakeSequenceWithTrimmedFiles(String token, Collection<File> files, String strainId) throws FileNotFoundException {
        MongoCollection<Document> collection = database.getCollection("sequences");

        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        List<ObjectId> trimmedIds = new ArrayList<>();

        for (File file : files) {
            ObjectId id = gridFSBucket.uploadFromStream(file.getName(), new FileInputStream(file));
            trimmedIds.add(id);
        }

        Document document = new Document()
                .append("genomeToolToken", token)
                .append("strain", new ObjectId(strainId))
                .append("trimmedPair", trimmedIds);

        collection.insertOne(document);
        return document.getObjectId("_id").toString();
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
    public void insertFakeReference(String strainId) {
        MongoCollection<Document> collection = database.getCollection("references");
        collection.insertOne(new Document()
                .append("strain", new ObjectId(strainId)));
    }

    @Override
    public void insertFakeReferenceWithDate(String date) {
        MongoCollection<Document> collection = database.getCollection("references");
        collection.insertOne(new Document()
                .append("createdAt", formatDate(date, "yyyy-MM-dd HH:mm:ss.SSS"))
        );
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
    public String insertFakeReferenceWithFile(File file, String strainId) throws FileNotFoundException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        ObjectId id = gridFSBucket.uploadFromStream(file.getName(), new FileInputStream(file));

        MongoCollection<Document> collection = database.getCollection("references");
        Document document = new Document()
                .append("file", id)
                .append("strain", new ObjectId(strainId));
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public String insertFakeStrain(String key) {
        MongoCollection<Document> collection = database.getCollection("strains");
        Document document = new Document()
                .append("keys", Collections.singletonList(key))
                .append("name", "anyName");
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public String insertFakeStrain(String key, String name) {
        MongoCollection<Document> collection = database.getCollection("strains");
        Document document = new Document()
                .append("keys", Collections.singletonList(key))
                .append("name", name);
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public boolean strainExists(String key) {
        MongoCollection<Document> collection = database.getCollection("strains");
        return collection.find(eq("keys", key)).first() != null;
    }

    @Override
    public String insertFakeReport(String token, String strainId) {
        MongoCollection<Document> collection = database.getCollection("reports");
        Document document = new Document()
                .append("name", "Fake report")
                .append("strain", new ObjectId(strainId))
                .append("genomeToolToken", token);
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public String insertFakeReportWithDate(String token, String strainId, String date) {
        MongoCollection<Document> collection = database.getCollection("reports");
        Document document = new Document()
                .append("name", "Fake report")
                .append("strain", new ObjectId(strainId))
                .append("genomeToolToken", token)
                .append("requestDate", formatDate(date, "yyyy-MM-dd HH:mm:ss.SSS"));
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public String insertFakeReportWithFilesSetToFalse(String token, String strainId) {
        MongoCollection<Document> collection = database.getCollection("reports");
        Document document = new Document()
                .append("name", "Another fake report")
                .append("strain", new ObjectId(strainId))
                .append("genomeToolToken", token)
                .append("files", false);
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public String insertFakeReportWithFile(String field, File file) throws FileNotFoundException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        ObjectId id = gridFSBucket.uploadFromStream(file.getName(), new FileInputStream(file));

        MongoCollection<Document> collection = database.getCollection("reports");
        Document document = new Document("files", new Document(field, id));
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public String insertFakeReportWithLog(File file) throws FileNotFoundException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        ObjectId id = gridFSBucket.uploadFromStream(file.getName(), new FileInputStream(file));

        MongoCollection<Document> collection = database.getCollection("reports");
        Document document = new Document("log", id);
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public InputStream getFileStream(String id) throws IOException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024 * 8);
        gridFSBucket.downloadToStream(new ObjectId(id), outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();
        return inputStream;
    }

    @Override
    public void createUser() throws InvalidKeySpecException, NoSuchAlgorithmException {
        MongoCollection<Document> collection = database.getCollection("users");
        EncryptedPassword password = new EncryptedPassword("password");
        Document document = new Document()
                .append("username", "test")
                .append("password", password.getHash())
                .append("salt", password.getSalt());
        collection.insertOne(document);
    }

    private static String formatDate(String date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String[] dateFields = date.split("/");
        LocalDateTime dateTime = LocalDateTime.of(
                Integer.parseInt(dateFields[0]), Integer.parseInt(dateFields[1]), Integer.parseInt(dateFields[2]), 0, 0);
        return dateTime.format(formatter);
    }
}
