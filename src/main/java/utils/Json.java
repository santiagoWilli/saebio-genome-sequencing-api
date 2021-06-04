package utils;

import java.util.Map;

public class Json {
    public static String id(String id) {
        return "{\"id\":\"" + id + "\"}";
    }

    public static String message(String message) {
        return "{\"message\":\"" + message + "\"}";
    }

    public static String custom(Map<String, String> map) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json.append("\"")
                    .append(entry.getKey()).append("\":\"")
                    .append(entry.getValue()).append("\",");
        }
        json.deleteCharAt(json.length() - 1);
        json.append("}");
        return json.toString();
    }
}
