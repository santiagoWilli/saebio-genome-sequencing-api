package utils;

import java.util.Map;

public class RequestParams {
    private final Map<String, String> path;
    private final Map<String, String[]> query;

    public RequestParams(Map<String, String> path, Map<String, String[]> query) {
        this.path = path;
        this.query = query;
    }

    public Map<String, String> path() {
        return path;
    }

    public Map<String, String[]> query() {
        return query;
    }
}
