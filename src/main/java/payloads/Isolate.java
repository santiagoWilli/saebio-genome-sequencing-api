package payloads;

import java.io.File;
import java.util.Map;

public abstract class Isolate extends Multipart {
    protected Isolate(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    public String getName() {
        return files.keySet().iterator().next();
    }

    public String getStrainKey() {
        return fileNameFirstFieldRemove("[0-9]").toLowerCase();
    }

    public String getIsolateCode() {
        return fileNameFirstFieldRemove("[^0-9]");
    }

    private String fileNameFirstFieldRemove(String regex) {
        return getName()
                .split("[_\\-]")[0]
                .replaceAll(regex, "");
    }
}
