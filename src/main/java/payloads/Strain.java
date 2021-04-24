package payloads;

import java.util.Map;

public class Strain extends RequestParameters implements Validable {
    public Strain(Map<String, String> parameters) {
        super(parameters);
    }

    @Override
    public boolean isValid() {
        return parameters.get("name") != null && parameters.get("key") != null;
    }
}
