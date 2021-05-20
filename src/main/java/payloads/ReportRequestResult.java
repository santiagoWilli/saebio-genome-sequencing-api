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
        return getStatusCode() != 2;
    }
}