package handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataaccess.DataAccess;
import payloads.EmptyPayload;
import utils.Answer;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class SequencesGetTrimmedPairHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public SequencesGetTrimmedPairHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, Map<String, String> requestParams) {
        String sequenceJson = dataAccess.getSequence(requestParams.get(":id"));
        if (sequenceJson.isEmpty()) return Answer.notFound();

        Map<String, Object> sequence;
        try {
            sequence = new ObjectMapper().readValue(sequenceJson, new TypeReference<HashMap<String,Object>>(){});

            if (sequence.get("trimmedPair") instanceof Boolean) return new Answer(211, "The sequence does not have a trimmed pair due to an internal error");

            ArrayList<Map<String, String>> trimmedPair = (ArrayList<Map<String, String>>) sequence.get("trimmedPair");

            if (trimmedPair == null) return new Answer(210, "The sequence does not have its trimmed pair yet");

            File zipFile = new File("temp/trimmed" + UUID.randomUUID().toString() + ".zip");
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            for (Map<String, String> trimmedFile : trimmedPair) {
                InputStream fileStream = dataAccess.getFileStream(trimmedFile.get("$oid"));
                ZipEntry zipEntry = new ZipEntry(dataAccess.getTrimmedFileName(trimmedFile.get("$oid")));
                writeNextZipEntry(zipOutputStream, zipEntry, fileStream);
            }
            zipOutputStream.close();
            fileOutputStream.close();

            Path path = Paths.get(zipFile.toURI());
            InputStream fileStream = Files.newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE);

            return Answer.withFile(fileStream, "application/zip");
        } catch (IOException e) {
            e.printStackTrace();
            return Answer.serverError(e.getMessage());
        }
    }

    private void writeNextZipEntry(ZipOutputStream zipOutputStream, ZipEntry zipEntry, InputStream fileStream) throws IOException {
        zipOutputStream.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fileStream.read(bytes)) >= 0) {
            zipOutputStream.write(bytes, 0, length);
        }
        fileStream.close();
    }
}
