package payloads;

import java.io.File;
import java.util.Map;

public class ReportRequestResult extends RequestResult implements Validable {
    public ReportRequestResult(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    @Override
    public boolean isValid() {
        if (fields.get("status") == null || getSequenceToken() == null) return false;
        if (getStatusCode() == 2) {
            return files.keySet().size() == 1 && files.keySet().iterator().next().endsWith(".html");
        }
        return true;
    }

    public Map.Entry<String, File> getFile() {
        return files.entrySet().iterator().next();
    }
}