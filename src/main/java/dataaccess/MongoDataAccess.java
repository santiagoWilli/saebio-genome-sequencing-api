package dataaccess;

import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.IndexOptions;
import dataaccess.exceptions.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import payloads.*;
import utils.EncryptedPassword;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
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
                .append("strain", getStrain(sequence.getStrainKey()).getObjectId("_id"))
                .append("code", sequence.getIsolateCode())
                .append("originalFilenames", sequence.getOriginalFileNames())
                .append("genomeToolToken", genomeToolToken)
                .append("trimRequestDate", formatDate(LocalDateTime.now(ZoneOffset.UTC), "yyyy-MM-dd HH:mm:ss.SSS"));
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public String createSequenceAlreadyTrimmed(Sequence sequence) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        Document document = new Document("sequenceDate", formatDate(sequence.getDate()))
                .append("strain", getStrain(sequence.getStrainKey()).getObjectId("_id"))
                .append("code", sequence.getIsolateCode())
                .append("originalFilenames", sequence.getOriginalFileNames());
        collection.insertOne(document);

        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        List<ObjectId> trimmedIds = new ArrayList<>();

        for (Map.Entry<String, File> entry : sequence.getFiles().entrySet()) {
            try (InputStream stream = new FileInputStream(entry.getValue())) {
                ObjectId id = gridFSBucket.uploadFromStream(entry.getKey(), stream);
                trimmedIds.add(id);
            } catch (IOException e) {
                e.getStackTrace();
                return null;
            }
        }
        collection.updateOne(eq(document.getObjectId("_id")), set("trimmedPair", trimmedIds));

        return document.getObjectId("_id").toString();    }

    @Override
    public UploadCode uploadTrimmedFiles(TrimRequestResult trimResult) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        if (collection.countDocuments(eq("genomeToolToken", trimResult.getToken())) < 1) {
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
        collection.updateOne(eq("genomeToolToken", trimResult.getToken()), set("trimmedPair", trimmedIds));
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
        return findAllFromCollection("sequences");
    }

    @Override
    public String getAllSequences(String strainId) {
        return findAllFromCollectionWithGivenStrain("sequences", strainId);
    }

    @Override
    public String getSequence(String id) {
        if (!ObjectId.isValid(id)) return "";
        MongoCollection<Document> collection = database.getCollection("sequences");

        ArrayList<Document> result = collection.aggregate(Arrays.asList(
                match(eq("_id", new ObjectId(id))),
                lookup("strains", "strain", "_id", "strain"),
                unwind("$strain"),
                project(fields(exclude("strain.keys")))
        )).into(new ArrayList<>());
        return result.size() < 1 ? "" : result.get(0).toJson();
    }

    @Override
    public boolean sequenceAlreadyExists(Sequence sequence) {
        MongoCollection<Document> collection = database.getCollection("sequences");

        final ObjectId strainId = getStrain(sequence.getStrainKey()).getObjectId("_id");

        return collection.countDocuments(
                and(
                    eq("strain", strainId),
                    eq("code", sequence.getIsolateCode()),
                    eq("sequenceDate", formatDate(sequence.getDate()))
                )) > 0;
    }

    @Override
    public String getFileName(String id) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSFile file = gridFSBucket.find(eq("_id", new ObjectId(id))).first();
        return file != null ? file.getFilename() : null;
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
    public String uploadReference(Reference reference) throws IOException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);

        ObjectId id = gridFSBucket.uploadFromStream(reference.getName(), new FileInputStream(reference.getFile()));

        MongoCollection<Document> collection = database.getCollection("references");
        Document document = new Document()
                .append("strain", getStrain(reference.getStrainKey()).getObjectId("_id"))
                .append("code", reference.getIsolateCode())
                .append("file", id);
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public String getAllReferences() {
        return findAllFromCollection("references");
    }

    @Override
    public String getReference(String id) {
        if (!ObjectId.isValid(id)) return "";
        MongoCollection<Document> collection = database.getCollection("references");
        final Document document = collection.find(eq("_id", new ObjectId(id))).first();
        return document == null ? "" : document.toJson();
    }

    @Override
    public boolean referenceAlreadyExists(Reference reference) {
        MongoCollection<Document> collection = database.getCollection("references");

        final ObjectId strainId = getStrain(reference.getStrainKey()).getObjectId("_id");

        return collection.countDocuments(
                and(
                        eq("strain", strainId),
                        eq("code", reference.getIsolateCode())
                )) > 0;
    }

    @Override
    public String getAllStrains() {
        return findAllFromCollection("strains");
    }

    @Override
    public boolean strainExists(String key) {
        MongoCollection<Document> collection = database.getCollection("strains");
        return collection.find(eq("keys", key)).first() != null;
    }

    @Override
    public String createStrain(Strain strain) throws UniquenessViolationException {
        MongoCollection<Document> collection = database.getCollection("strains");
        collection.createIndex(new Document("name", 1), new IndexOptions().unique(true));

        for (String key : strain.getKeys()) {
            if (collection.countDocuments(eq("keys", key)) > 0) throw new UniquenessViolationException("Strain key already exists");
        }
        Document document = new Document()
                .append("name", strain.getName())
                .append("keys", strain.getKeys());
        try {
            collection.insertOne(document);
        } catch (Exception e) {
            throw new UniquenessViolationException("Strain name already exists");
        }
        return document.getObjectId("_id").toString();
    }

    @Override
    public boolean updateStrainKeys(String id, StrainKeys keys) throws UniquenessViolationException {
        MongoCollection<Document> collection = database.getCollection("strains");
        if (!ObjectId.isValid(id) || collection.countDocuments(eq("_id", new ObjectId(id))) < 1) return false;
        for (String key : keys.getKeys()) {
            if (collection.countDocuments(eq("keys", key)) > 0) {
                Document document = collection.find(eq("keys", key)).first();
                if (!document.getObjectId("_id").toString().equals(id)) throw new UniquenessViolationException("Strain key already exists");
            }
        }
        collection.updateOne(
                eq("_id", new ObjectId(id)),
                set("keys", keys.getKeys())
        );
        return true;
    }

    @Override
    public boolean deleteStrain(String id) throws DocumentPointsToStrainException {
        if (!ObjectId.isValid(id)) return false;
        MongoCollection<Document> collection = database.getCollection("sequences");
        if (collection.countDocuments(eq("strain", new ObjectId(id))) > 0) throw new DocumentPointsToStrainException();

        collection = database.getCollection("strains");
        return collection.deleteOne(eq("_id", new ObjectId(id))).getDeletedCount() > 0;
    }

    @Override
    public boolean referenceAndSequencesShareTheSameStrain(String referenceId, Set<String> sequencesIds) {
        Document reference = database.getCollection("references")
                .find(eq("_id", new ObjectId(referenceId))).first();
        String strainId = reference.getObjectId("strain").toString();

        MongoCollection<Document> collection = database.getCollection("sequences");
        for (String sequenceId : sequencesIds) {
            Document sequence = collection.find(eq("_id", new ObjectId(sequenceId))).first();
            if (!sequence.getObjectId("strain").toString().equals(strainId)) return false;
        }
        return true;
    }

    @Override
    public String createReport(ReportRequest reportRequest, String token) {
        String date = formatDate(LocalDateTime.now(ZoneOffset.UTC), "dd/MM/yyyy HH:mm");

        MongoCollection<Document> collection = database.getCollection("references");
        Document strain = collection.aggregate(Arrays.asList(
                match(eq("_id", new ObjectId(reportRequest.getReference()))),
                lookup("strains", "strain", "_id", "strain"),
                unwind("$strain"),
                project(fields(include("name"), computed("name", "$strain.name"), computed("_id", "$strain._id")))
        )).into(new ArrayList<>()).get(0);

        collection = database.getCollection("reports");
        Document document = new Document()
                .append("name", strain.getString("name") + " " + date)
                .append("strain", strain.getObjectId("_id"))
                .append("sequences", reportRequest.getSequences().stream().map(ObjectId::new).collect(Collectors.toList()))
                .append("reference", new ObjectId(reportRequest.getReference()))
                .append("genomeToolToken", token)
                .append("requestDate", date);
        collection.insertOne(document);
        return document.getObjectId("_id").toString();
    }

    @Override
    public List<String> getSequenceTrimmedFilesIds(String sequenceId) {
        MongoCollection<Document> collection = database.getCollection("sequences");
        final Document document = collection.find(eq("_id", new ObjectId(sequenceId))).first();
        return document.getList("trimmedPair", ObjectId.class)
                .stream()
                .map(ObjectId::toString)
                .collect(Collectors.toList());
    }

    @Override
    public String getReferenceFileId(String referenceId) {
        MongoCollection<Document> collection = database.getCollection("references");
        final Document document = collection.find(eq("_id", new ObjectId(referenceId))).first();
        return document.getObjectId("file").toString();
    }

    @Override
    public UploadCode uploadReportFiles(ReportRequestResult reportResult) {
        MongoCollection<Document> reportsCollection = database.getCollection("reports");
        if (reportsCollection.countDocuments(eq("genomeToolToken", reportResult.getToken())) < 1) {
            return UploadCode.NOT_FOUND;
        }

        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        ObjectId reportFileId, referenceFileId;

        try (InputStream reportStream = new FileInputStream(reportResult.getReport().getValue());
             InputStream referenceStream = new FileInputStream(reportResult.getReference().getValue())) {
            reportFileId = gridFSBucket.uploadFromStream(reportResult.getReport().getKey(), reportStream);
            referenceFileId = gridFSBucket.uploadFromStream(reportResult.getReference().getKey(), referenceStream);
        } catch (IOException e) {
            e.getStackTrace();
            return UploadCode.WRITE_FAILED;
        }

        Document report = reportsCollection.find(eq("genomeToolToken", reportResult.getToken())).first();
        MongoCollection<Document> collection = database.getCollection("references");
        Document reference = new Document()
                .append("strain", report.getObjectId("strain"))
                .append("reportName", report.getString("name"))
                .append("file", referenceFileId);
        collection.insertOne(reference);

        reportsCollection.updateOne(eq("genomeToolToken", reportResult.getToken()),
                set("files", new Document()
                        .append("report", reportFileId)
                        .append("reference", reference.getObjectId("_id"))));
        return UploadCode.OK;
    }

    @Override
    public boolean setReportFileToFalse(ReportRequestResult reportResult) {
        MongoCollection<Document> collection = database.getCollection("reports");
        if (collection.countDocuments(eq("genomeToolToken", reportResult.getToken())) < 1) return false;

        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        ObjectId logId;
        if (reportResult.getLog() != null) {
            try (InputStream logStream = new FileInputStream(reportResult.getLog().getValue())) {
                logId = gridFSBucket.uploadFromStream(reportResult.getLog().getKey(), logStream);
                collection.updateOne(eq("genomeToolToken", reportResult.getToken()), set("log", logId));
            } catch (IOException e) {
                e.getStackTrace();
            }
        }

        collection.updateOne(eq("genomeToolToken", reportResult.getToken()), set("files", false));
        return true;
    }

    @Override
    public String getAllReports() {
        return findAllFromCollection("reports");
    }

    @Override
    public String getReport(String id) {
        if (!ObjectId.isValid(id)) return "";
        MongoCollection<Document> collection = database.getCollection("reports");
        final Document document = collection.find(eq("_id", new ObjectId(id))).first();
        return document == null ? "" : document.toJson();
    }

    @Override
    public String getReportFileId(String id) {
        MongoCollection<Document> collection = database.getCollection("reports");
        final Document document = collection.find(eq("_id", new ObjectId(id))).first();
        final Document filesDoc = document != null ? document.get("files", Document.class) : null;
        final ObjectId fileId = filesDoc != null ? filesDoc.getObjectId("report") : null;
        return fileId == null ? null : fileId.toString();
    }

    @Override
    public String getReportLogId(String id) {
        MongoCollection<Document> collection = database.getCollection("reports");
        final Document report = collection.find(eq("_id", new ObjectId(id))).first();
        final ObjectId fileId = report != null ? report.getObjectId("log") : null;
        return fileId == null ? null : fileId.toString();
    }

    @Override
    public boolean login(UserAuthentication authentication) throws UserNotFoundException {
        MongoCollection<Document> collection = database.getCollection("users");
        final Document user = collection.find(eq("username", authentication.getUsername())).first();
        if (user == null) throw new UserNotFoundException();
        try {
            String securedPassword = user.getString("password");
            String securedSalt = user.getString("salt");
            EncryptedPassword password = new EncryptedPassword(authentication.getPassword(), securedSalt);
            return password.getHash().equals(securedPassword);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Document getStrain(String key) {
        MongoCollection<Document> collection = database.getCollection("strains");
        return collection.find(eq("keys", key)).first();
    }

    private String findAllFromCollection(String collectionName) {
        return findAllFromCollection(collectionName, null);
    }

    private String findAllFromCollection(String collectionName, Bson filter) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<String> documents = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(filter == null ? new Document() : filter).iterator()) {
            while (cursor.hasNext()) {
                documents.add(cursor.next().toJson());
            }
        }
        return "[" + String.join(", ", documents) + "]";
    }

    private String findAllFromCollectionWithGivenStrain(String collectionName, String strainId) {
        if (!ObjectId.isValid(strainId)) return "[]";
        return findAllFromCollection(collectionName, eq("strain", new ObjectId(strainId)));
    }

    private static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    private static String formatDate(LocalDateTime date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
}
