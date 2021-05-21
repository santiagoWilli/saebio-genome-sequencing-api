package payloads;

import java.io.File;
import java.util.Map;

public abstract class RequestResult extends Multipart {
    protected RequestResult(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    public String getToken() {
        return fields.get("token");
    }

    public int getStatusCode() {
        return Integer.parseInt(fields.get("status"));
    }
}
