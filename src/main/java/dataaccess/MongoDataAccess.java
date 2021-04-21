package dataaccess;

import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import payloads.Reference;
import payloads.Sequence;
import payloads.TrimRequestResult;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                .append("originalFilenames", sequence.getOriginalFileNames())
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
        List<ObjectId> trimmedIds = new ArrayList<>();

        for (Map.Entry<String, File> entry : trimResult.getFiles().entrySet()) {
            try (InputStream stream = new FileInputStream(entry.getValue())) {
                ObjectId id = gridFSBucket.uploadFromStream(entry.getKey(), stream);
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

    @Override
    public String getSequence(String id) {
        if (!ObjectId.isValid(id)) return "";
        MongoCollection<Document> collection = database.getCollection("sequences");
        final Document sequence = collection.find(eq("_id", new ObjectId(id))).first();
        return sequence == null ? "" : sequence.toJson();
    }

    @Override
    public String getTrimmedFileName(String id) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSFile file = gridFSBucket.find(eq("_id", new ObjectId(id))).first();
        return file != null ? file.getFilename() : null;
    }

    @Override
    public InputStream getTrimmedFileStream(String id) throws IOException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024 * 8);
        gridFSBucket.downloadToStream(new ObjectId(id), outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();
        return inputStream;
    }

    @Override
    public String uploadReference(Reference reference) throws IOException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);

        ObjectId id = gridFSBucket.uploadFromStream(reference.getName(), new FileInputStream(reference.getFile()));

        MongoCollection<Document> collection = database.getCollection("references");
        Document document = new Document()
                .append("strain", reference.getStrain())
                .append("code", reference.getCode())
                .append("file", id);
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
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
