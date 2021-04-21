package unit.payloads;

import org.junit.jupiter.api.Test;
import payloads.Reference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class Reference_ {
    static final String[] VALID_NAMES = {"Kneu123456-referencia.fa", "Kneu24-referencia.fa", "kneu1-referencia.gbf"};

    @Test
    public void valid_if_fileNameIsCorrect() {
        iterateThroughFileNames(VALID_NAMES, true);
    }

    private static void iterateThroughFileNames(String[] fileNames, boolean expected) {
        for (String fileName : fileNames) {
            Reference reference = getSequenceFrom(fileName);
            assertThat(reference.isValid()).isEqualTo(expected);
        }
    }

    private static Reference getSequenceFrom(String fileName) {
        Map<String, File> fileMap = new HashMap<>();
        File file = mock(File.class);
        fileMap.put(fileName, file);
        return new Reference(null, fileMap);
    }
}
