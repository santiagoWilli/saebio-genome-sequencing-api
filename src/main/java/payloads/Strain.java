package payloads;

import java.util.Map;
import java.util.regex.Pattern;

public class Strain extends RequestParameters implements Validable {
    public Strain(Map<String, String> parameters) {
        super(parameters);
    }

    public String getName() {
        return parameters.get("name");
    }

    public String getKey() {
        return parameters.get("key").toLowerCase();
    }

    @Override
    public boolean isValid() {
        if (parameters.get("name") != null && parameters.get("key") != null) {
            return !parameters.get("name").isEmpty() &&
                    !parameters.get("key").isEmpty() &&
                    !onlyContainsAlphabeticChars(parameters.get("key"));
        }
        return false;
    }

    private static boolean onlyContainsAlphabeticChars(String string) {
        Pattern p = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
        return p.matcher(string).find();
    }
}
