package payloads;

import utils.StrainMap;

import java.io.File;
import java.util.Map;

public class Reference extends Multipart implements Validable {
    public Reference(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    public String getName() {
        return files.keySet().iterator().next();
    }

    public String getStrain() {
        return StrainMap.get(strainKey());
    }

    @Override
    public boolean isValid() {
        if (files.size() != 1) return false;
        return files.keySet().iterator().next().matches(fileNameRegex());
    }

    private String strainKey() {
        return getName().split("[_\\-]")[0].replaceAll("[0-9]", "");
    }

    private static String fileNameRegex() {
        return "[a-zA-Z]+[0-9]{1,6}(-|_)referencia.(fa|gbf)";
    }
}
