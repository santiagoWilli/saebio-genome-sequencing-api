package payloads;

import java.util.Map;

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
        return parameters.get("name") != null && parameters.get("key") != null;
    }
}
