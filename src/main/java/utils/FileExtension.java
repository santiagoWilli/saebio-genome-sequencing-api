package utils;

import java.util.AbstractMap;
import java.util.Map;

public class FileExtension {
    private static final Map<String, String> MAP = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("application/zip", ".zip"),
            new AbstractMap.SimpleEntry<>("text/x-fasta", ".fa"),
            new AbstractMap.SimpleEntry<>("text/html", ".html"),
            new AbstractMap.SimpleEntry<>("text/plain", ".log"),
            new AbstractMap.SimpleEntry<>("text/csv", ".csv"),
            new AbstractMap.SimpleEntry<>("text/x-nh", ".newick"),
            new AbstractMap.SimpleEntry<>("application/octet-stream", "")
    );

    private FileExtension() {}

    public static String forMimeType(String type) {
        return MAP.containsKey(type) ? MAP.get(type) : MAP.get("application/octet-stream");
    }
}
