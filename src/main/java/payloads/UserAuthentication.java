package payloads;

import java.util.Map;

public class UserAuthentication extends RequestParameters implements Validable {
    public UserAuthentication(Map<String, String[]> parameters) {
        super(parameters);
    }

    @Override
    public boolean isValid() {
        return parameters.get("username") != null &&
                parameters.get("password") != null &&
                !getUsername().isEmpty() &&
                !getPassword().isEmpty();
    }

    public String getUsername() {
        return parameters.get("username")[0];
    }

    public String getPassword() {
        return parameters.get("password")[0];
    }
}
