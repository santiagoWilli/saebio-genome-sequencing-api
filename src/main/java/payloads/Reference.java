package payloads;

import java.io.File;
import java.util.Map;

public class Reference extends Multipart implements Validable {
    public Reference(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
