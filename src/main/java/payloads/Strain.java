package payloads;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Strain extends RequestParameters implements Validable {
    public Strain(Map<String, String[]> parameters) {
        super(parameters);
    }

    public String getName() {
        return parameters.get("name")[0];
    }

    public List<String> getKeys() {
        return Arrays.stream(parameters.get("key"))
                .filter(k -> !k.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid() {
        if (parameters.get("name") != null && parameters.get("key") != null) {
            return !getName().isEmpty() &&
                    getKeys().size() > 0 &&
                    !keysContainNonAlphabeticChars();
        }
        return false;
    }

    private boolean keysContainNonAlphabeticChars() {
        Pattern p = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
        for (String key : getKeys()) {
            if (p.matcher(key).find()) return true;
        }
        return false;
    }
}
