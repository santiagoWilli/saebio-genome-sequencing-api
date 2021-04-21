package utils;

import java.util.AbstractMap;
import java.util.Map;

public final class StrainMap {
    private static final Map<String, String> MAP = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("kp", "Klebsiella pneumoniae"),
            new AbstractMap.SimpleEntry<>("kpneu", "Klebsiella pneumoniae"),
            new AbstractMap.SimpleEntry<>("kneu", "Klebsiella pneumoniae")
    );

    private StrainMap() {}

    public static String get(String key) {
        return MAP.get(key.toLowerCase());
    }
}
