package unit.payloads;

import org.junit.jupiter.api.Test;
import payloads.Reference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class Reference_ {
    static final String[] VALID_NAMES = {"Kneu123456-referencia.fa", "Kneu24-referencia.fa", "kneu1-referencia.gbf", "kneu1_referencia.gbf"};
    static final String[] INVALID_NAMES = {"Kneu-referencia.fa", "referencia.fa", "kneu123456-referencia.fq", "referencia-kneu123456.fa"};

    @Test
    public void valid_if_fileNameIsCorrect() {
        iterateThroughFileNames(VALID_NAMES, true);
    }

    @Test
    public void invalid_if_fileNameIsNotCorrect() {
        iterateThroughFileNames(INVALID_NAMES, false);
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
