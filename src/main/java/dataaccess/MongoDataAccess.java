package dataaccess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.Document;
import org.bson.types.ObjectId;
import payloads.Sequence;
import payloads.TrimRequestResult;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        MongoCollection<Document> collection = database.getCollection("sequences");
        if (collection.countDocuments(eq("genomeToolToken", trimResult.getSequenceToken())) < 1) {
            return UploadCode.NOT_FOUND;
        }

        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        List<Part> fileParts = trimResult.getFileParts();
        List<ObjectId> trimmedIds = new ArrayList<>();

        for (Part filePart : fileParts) {
            try (InputStream stream = filePart.getInputStream()) {
                ObjectId id = gridFSBucket.uploadFromStream(filePart.getSubmittedFileName(), stream);
                trimmedIds.add(id);
            } catch (IOException e) {
                e.getStackTrace();
                return UploadCode.WRITE_FAILED;
            }
        }
        collection.updateOne(eq("genomeToolToken", trimResult.getSequenceToken()), set("trimmedPair", trimmedIds));
        return UploadCode.OK;
    }

    @Override
    public boolean setSequenceTrimToFalse(String token) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        if (collection.countDocuments(eq("genomeToolToken", token)) < 1) return false;
        collection.updateOne(eq("genomeToolToken", token), set("trimmedPair", false));
        return true;
    }

    @Override
    public String getAllSequences() {
        MongoCollection<Document> collection = database.getCollection("sequences");
        List<String> files = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                files.add(cursor.next().toJson());
            }
        }
        return "[" + String.join(", ", files) + "]";
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
