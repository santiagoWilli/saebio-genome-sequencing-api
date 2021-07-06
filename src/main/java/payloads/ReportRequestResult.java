package payloads;

import java.io.File;
import java.util.Map;

public class ReportRequestResult extends RequestResult implements Validable {
    public ReportRequestResult(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    @Override
    public boolean isValid() {
        if (fields.get("status") == null || getToken() == null) return false;
        if (getStatusCode() == 2) {
            if (files.keySet().size() != 2) return false;
            String[] filenames = new String[2];
            int i = 0;
            for (String filename : files.keySet()) filenames[i++] = filename;
            return filenames[0].endsWith(".zip") && filenames[1].endsWith(".fa") ||
                    filenames[1].endsWith(".zip") && filenames[0].endsWith(".fa");
        }
        return true;
    }

    public Map.Entry<String, File> getReportFiles() {
        return getMapEntryWithExtension(".zip");
    }

    public Map.Entry<String, File> getReference() {
        return getMapEntryWithExtension(".fa");
    }

    public Map.Entry<String, File> getLog() {
        return getMapEntryWithExtension(".log");
    }

    private Map.Entry<String, File> getMapEntryWithExtension(String extension) {
        for (Map.Entry<String, File> file : files.entrySet()) {
            if (file.getKey().endsWith(extension)) return file;
        }
        return null;
    }
}