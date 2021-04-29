package payloads;

import java.util.Map;

public class Strain extends AbstractStrain implements Validable {
    public Strain(Map<String, String[]> parameters) {
        super(parameters);
    }

    public String getName() {
        return parameters.get("name")[0];
    }

    @Override
    public boolean isValid() {
        if (parameters.get("name") != null && parameters.get("key") != null) {
            return !getName().isEmpty() &&
                    getKeys().size() > 0 &&
                    keysContainOnlyAlphabeticChars();
        }
        return false;
    }
}
