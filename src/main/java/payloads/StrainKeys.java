package payloads;

import java.util.Map;

public class StrainKeys extends AbstractStrain implements Validable {
    public StrainKeys(Map<String, String[]> parameters) {
        super(parameters);
    }

    @Override
    public boolean isValid() {
        return parameters.get("key") != null &&
                getKeys().size() > 0;
    }
}
