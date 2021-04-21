package payloads;

import java.io.File;
import java.util.Map;

public class Reference extends Multipart implements Validable {
    public Reference(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    @Override
    public boolean isValid() {
        return files.keySet().iterator().next().matches(fileNameRegex());
    }

    private static String fileNameRegex() {
        return "[a-zA-Z]+[0-9]{1,6}(-|_)referencia.(fa|gbf)";
    }
}
