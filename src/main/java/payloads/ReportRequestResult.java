package payloads;

import java.io.File;
import java.util.Iterator;
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
            return filenames[0].endsWith(".html") && filenames[1].endsWith(".fa") ||
                    filenames[1].endsWith(".html") && filenames[0].endsWith(".fa");
        }
        return true;
    }

    public Map.Entry<String, File> getReport() {
        return getMapEntryWithExtension(".html");
    }

    public Map.Entry<String, File> getReference() {
        return getMapEntryWithExtension(".fa");
    }

    public Map.Entry<String, File> getLog() {
        return getMapEntryWithExtension(".log");
    }

    private Map.Entry<String, File> getMapEntryWithExtension(String extension) {
        System.out.println(files.entrySet());
        for (Map.Entry<String, File> file : files.entrySet()) {
            System.out.println(file.getKey());
            if (file.getKey().endsWith(extension)) return file;
        }
        return null;
    }
}