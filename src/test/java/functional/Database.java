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
    void insertFakeSequenceWithTrimmedFiles(String token, Collection<File> files) throws FileNotFoundException;

    void empty(String collection);

    boolean referenceExists(String id);
    void insertFakeReference();
    String insertFakeReferenceWithFile(File file) throws FileNotFoundException;

    String insertFakeStrain(String key);
}
