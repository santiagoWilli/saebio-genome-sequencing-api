package payloads;

import java.util.Map;

public abstract class RequestParameters {
    private final Map<String, String> parameters;

    protected RequestParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
