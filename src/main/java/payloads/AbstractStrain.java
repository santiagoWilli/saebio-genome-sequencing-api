package payloads;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractStrain extends RequestParameters {
    protected AbstractStrain(Map<String, String[]> parameters) {
        super(parameters);
    }

    public List<String> getKeys() {
        return Arrays.stream(parameters.get("key"))
                .filter(k -> !k.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    protected boolean keysContainOnlyAlphabeticChars() {
        Pattern p = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
        for (String key : getKeys()) {
            if (p.matcher(key).find()) return false;
        }
        return true;
    }
}
