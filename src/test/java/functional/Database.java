package functional;

import java.io.IOException;
import java.util.Map;

public interface Database {
    Map<String, Object> get(String collection, String id) throws IOException;
}
