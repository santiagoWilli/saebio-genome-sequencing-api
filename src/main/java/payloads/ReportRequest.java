package payloads;

import java.util.*;
import java.util.stream.Collectors;

public class ReportRequest extends RequestParameters implements Validable {
    public ReportRequest(Map<String, String[]> parameters) {
        super(parameters);
    }

    @Override
    public boolean isValid() {
        if (parameters.get("sequences") == null || parameters.get("reference") == null) return false;
        return getSequences().size() > 0 && parameters.get("reference").length == 1 && !getReference().isEmpty();
    }

    public Set<String> getSequences() {
        return Arrays.stream(parameters.get("sequences"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    public String getReference() {
        return parameters.get("reference")[0];
    }
}
