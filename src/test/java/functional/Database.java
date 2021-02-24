package functional;

import java.io.IOException;
import java.util.Map;

public interface Database {
    Map<String, Object> get(String collection, String id) throws IOException;
    Map<String, Object> get(String collection, String field, String value) throws IOException;

    void delete(String collection, String field, String value);

    void insertFakeSequence(String token);
}
