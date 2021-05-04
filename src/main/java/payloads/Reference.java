package payloads;

import java.io.File;
import java.util.Map;

public class Reference extends Isolate implements Validable {
    public Reference(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    public File getFile() {
        return files.entrySet().iterator().next().getValue();
    }

    @Override
    public boolean isValid() {
        if (files.size() != 1) return false;
        return getName().matches(fileNameRegex());
    }

    private static String fileNameRegex() {
        return "[a-zA-Z]+[0-9]{1,6}(-|_)referencia.(fa|gbf)";
    }
}
