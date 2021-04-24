package payloads;

import java.util.Map;

public class Strain extends RequestParameters implements Validable {
    protected Strain(Map<String, String> parameters) {
        super(parameters);
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
