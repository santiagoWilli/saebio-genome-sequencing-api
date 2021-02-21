package unit;

import org.junit.jupiter.api.Test;
import payloads.Sequence;

import javax.servlet.http.Part;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
    public void invalid_if_notTwoParts() {
        Part part = mock(Part.class);
        Collection<Part> partCollection = new ArrayList<>();
        partCollection.add(part);
        Sequence sequence = new Sequence(partCollection);
        assertThat(sequence.isValid()).isEqualTo(false);

        for (int i = 0; i < 2; i++) partCollection.add(part);
        sequence = new Sequence(partCollection);
        assertThat(sequence.isValid()).isEqualTo(false);
    }

    @Test
    public void valid_if_partFilenamesAreCorrect() {
        iterateThroughPairs(VALID_PAIRS, true);
    }

    @Test
    public void invalid_if_partFilenamesAreIncorrect() {
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
    public void getDate_shouldReturn_aDateBasedOnPairFilenames() {
        Sequence sequence = getSequenceFrom(VALID_PAIRS[0]);
        assertThat(sequence.getDate()).isEqualTo(LocalDate.of(2020, 11, 23));
    }

    @Test
    public void getStrain_shouldReturn_theValueFromTheKeyInTheFilename() {
        Sequence sequence = getSequenceFrom(VALID_PAIRS[0]);
        assertThat(sequence.getStrain()).isEqualTo("Klebsiella pneumoniae");
    }

    @Test
    public void getOriginalFilenames_shouldReturn_pairFilenames() {
        Sequence sequence = getSequenceFrom(VALID_PAIRS[0]);
        assertThat(sequence.getOriginalFilenames()).isEqualTo(Arrays.asList("Kp1_231120_R1.fastq.gz", "Kp1_231120_R2.fastq.gz"));
    }

    private static void iterateThroughPairs(String[][] pairs, boolean expected) {
        for (String[] pair : pairs) {
            Sequence sequence = getSequenceFrom(pair);
            assertThat(sequence.isValid()).isEqualTo(expected);
        }
    }

    private static Sequence getSequenceFrom(String[] pair) {
        Collection<Part> partCollection = new ArrayList<>();
        for (String filename : pair) {
            Part part = mock(Part.class);
            when(part.getSubmittedFileName()).thenReturn(filename);
            partCollection.add(part);
        }
        return new Sequence(partCollection);
    }
}
