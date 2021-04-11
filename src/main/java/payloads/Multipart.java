package payloads;

import java.io.File;
import java.util.Map;

public abstract class Multipart {
    protected final Map<String, String> fields;
    protected final Map<String, File> files;

    protected Multipart(Map<String, String> fields, Map<String, File> files) {
        this.fields = fields;
        this.files = files;
    }

    public Map<String, File> getFiles() {
        return files;
    }
}
