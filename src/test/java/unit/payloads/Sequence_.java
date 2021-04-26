package unit.payloads;

import org.junit.jupiter.api.Test;
import payloads.Sequence;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class Sequence_ {
    static final String[][] VALID_PAIRS = {
            {"Kp1_231120_R1.fastq.gz", "Kp1_231120_R2.fastq.gz"},
            {"Kp1_231120_R1.fq.gz", "Kp1_231120_R2.fq.gz"},
            {"Kpneu1_191120_R1.fastq.gz", "Kpneu1_191120_R2.fastq.gz"}};
    static final String[][] INVALID_PAIRS = {
            {"Kpneu1_R1.fastq.gz", "Kpneu1_R2.fastq.gz"},
            {"Kpneu1_R1_050121.fastq.gz", "Kpneu1_R2_050121.fastq.gz"},
            {"Kp1_231120_R3.fastq.gz", "Kp1_231120_R4.fastq.gz"},
            {"Kp1_231120_R1.fasta.gz", "Kp1_231120_R2.fasta.gz"},
            {"Kp1_231120_R1.fastq", "Kp1_231120_R2.fastq"},
            {"R1_Kpneu1_050121.fastq.gz", "R2_Kpneu1_050121.fastq.gz"},
            {"R1_050121_Kpneu1.fastq.gz", "R2_050121_Kpneu1.fastq.gz"},
            {"060121_R1_Kp1.fastq.gz", "060121_R2_Kp1.fastq.gz"},
            {"050121_Kpneu1_R1.fastq.gz", "050121_Kpneu1_R2.fastq.gz"}};
    static final String[][] NOT_SEQUENCES = {
            {"Kp1_231120_R1.fastq.gz", "Kp1_231221_R2.fastq.gz"},
            {"Kp1_231120_R1.fq.gz", "Kp3_231120_R2.fq.gz"}};

    @Test
    public void invalid_if_notTwoFiles() {
        File file = mock(File.class);
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("1", file);
        Sequence sequence = new Sequence(null, fileMap);
        assertThat(sequence.isValid()).isEqualTo(false);

        for (int i = 0; i < 2; i++) fileMap.put(Integer.toString(i+2), file);
        sequence = new Sequence(null, fileMap);
        assertThat(sequence.isValid()).isEqualTo(false);
    }

    @Test
    public void valid_if_fileNamesAreCorrect() {
        iterateThroughPairs(VALID_PAIRS, true);
    }

    @Test
    public void invalid_if_fileNamesAreIncorrect() {
        iterateThroughPairs(INVALID_PAIRS, false);
    }

    @Test
    public void invalid_if_pairFilesAreTheSame() {
        String[][] sameFiles = {{VALID_PAIRS[0][0], VALID_PAIRS[0][0]}};
        iterateThroughPairs(sameFiles, false);
    }

    @Test
    public void invalid_if_pairDoesNotFormASequence() {
        iterateThroughPairs(NOT_SEQUENCES, false);
    }

    @Test
    public void getDate_shouldReturn_aDateBasedOnPairFileNames() {
        Sequence sequence = getSequenceFrom(VALID_PAIRS[0]);
        assertThat(sequence.getDate()).isEqualTo(LocalDate.of(2020, 11, 23));
    }

    @Test
    public void getStrain_shouldReturn_theKeyInTheFilenameInLowerCase() {
        Sequence sequence = getSequenceFrom(VALID_PAIRS[0]);
        assertThat(sequence.getStrainKey()).isEqualTo("kp");
    }

    @Test
    public void getOriginalFilenames_shouldReturn_pairFilenames() {
        Sequence sequence = getSequenceFrom(VALID_PAIRS[0]);
        assertThat(sequence.getOriginalFileNames()).isEqualTo(Arrays.asList("Kp1_231120_R1.fastq.gz", "Kp1_231120_R2.fastq.gz"));
    }

    private static void iterateThroughPairs(String[][] pairs, boolean expected) {
        for (String[] pair : pairs) {
            Sequence sequence = getSequenceFrom(pair);
            assertThat(sequence.isValid()).isEqualTo(expected);
        }
    }

    private static Sequence getSequenceFrom(String[] pair) {
        Map<String, File> fileMap = new HashMap<>();
        for (String fileName : pair) {
            File file = mock(File.class);
            fileMap.put(fileName, file);
        }
        return new Sequence(null, fileMap);
    }
}
