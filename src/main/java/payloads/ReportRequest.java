package payloads;

import java.util.Map;

public class ReportRequest extends RequestParameters implements Validable {
    public ReportRequest(Map<String, String[]> parameters) {
        super(parameters);
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
