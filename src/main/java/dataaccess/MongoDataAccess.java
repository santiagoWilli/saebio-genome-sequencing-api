package dataaccess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import payloads.Sequence;
import payloads.TrimRequestResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class MongoDataAccess implements DataAccess {
    private final MongoDatabase database;

    public MongoDataAccess() {
        database = Database.get();
    }

    @Override
    public String createSequence(Sequence sequence, String genomeToolToken) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        Document document = new Document("sequenceDate", formatDate(sequence.getDate()))
                .append("strain", sequence.getStrain())
                .append("originalFilenames", sequence.getOriginalFilenames())
                .append("genomeToolToken", genomeToolToken)
                .append("trimRequestDate", formatDate(LocalDateTime.now(ZoneOffset.UTC)));
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public UploadCode uploadTrimmedFile(TrimRequestResult trimResult) {
        return null;
    }

    @Override
    public boolean setSequenceTrimToFalse(String token) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        if (collection.countDocuments(eq("genomeToolToken", token)) < 1) return false;
        collection.updateOne(eq("genomeToolToken", token), set("trimmedPair", false));
        return true;
    }

    private static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    private static String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return date.format(formatter);
    }
}
