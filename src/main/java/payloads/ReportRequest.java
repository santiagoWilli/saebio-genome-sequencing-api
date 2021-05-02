package payloads;

import java.util.*;
import java.util.stream.Collectors;

public class ReportRequest extends RequestParameters implements Validable {
    public ReportRequest(Map<String, String[]> parameters) {
        super(parameters);
    }

    @Override
    public boolean isValid() {
        if (parameters.get("sequences") == null) return false;
        return getSequences().size() > 0;
    }

    public Set<String> getSequences() {
        return Arrays.stream(parameters.get("sequences"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
