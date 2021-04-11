package payloads;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class TrimRequestResult extends Multipart implements Validable {
    public TrimRequestResult(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    public String getSequenceToken() {
        return fields.get("token");
    }

    public int getStatusCode() {
        return Integer.parseInt(fields.get("status"));
    }

    @Override
    public boolean isValid() {
        if (fields.get("status") == null || getSequenceToken() == null) return false;
        if (getStatusCode() == 2) {
            if (files.keySet().size() != 2) return false;
            for (String fileName : files.keySet()) if (!fileIsFastq(fileName)) return false;
        }
        return true;
    }

    private boolean fileIsFastq(String fileName) {
        String[] extensions = new String[] {".fq.gz", ".fastq.gz", ".fq", ".fastq"};
        return Arrays.stream(extensions)
                .anyMatch(fileName::endsWith);
    }
}
