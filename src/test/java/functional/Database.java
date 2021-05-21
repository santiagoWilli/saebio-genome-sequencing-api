package functional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public interface Database {
    Map<String, Object> get(String collection, String id) throws IOException;
    Map<String, Object> get(String collection, String field, String value) throws IOException;

    void insertFakeSequence(String token);
    void insertFakeSequence(String token, String strainId);
    String insertFakeSequenceWithTrimmedFiles(String token, Collection<File> files, String strainId) throws FileNotFoundException;

    void empty(String collection);

    boolean referenceExists(String id);
    void insertFakeReference();
    String insertFakeReferenceWithFile(File file) throws FileNotFoundException;

    String insertFakeReferenceWithFile(File file, String strainId) throws FileNotFoundException;

    String insertFakeStrain(String key);
    String insertFakeStrain(String key, String name);
    boolean strainExists(String id);

    String insertFakeReport(String token);
}
