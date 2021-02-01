package unit;

import org.junit.jupiter.api.Test;
import payloads.Sequence;

import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class Sequence_ {
    static final String[][] VALID_PAIRS = {
            {"Kp1_231120_R1.fastq", "Kp1_231120_R2.fastq"},
            {"Kpneu1_191120_R1.fastq", "Kpneu1_191120_R2.fastq"}};

    @Test
    public void invalid_if_onlyOnePart() {
        Part part = mock(Part.class);
        Collection<Part> onePartCollection = new ArrayList<>();
        onePartCollection.add(part);
        Sequence sequence = new Sequence(onePartCollection);
        assertThat(sequence.isValid()).isEqualTo(false);
    }

    @Test
    public void valid_if_partFilenamesAreCorrect() {
        for (String[] pair : VALID_PAIRS) {
            Collection<Part> onePartCollection = new ArrayList<>();
            for (String filename : pair) {
                Part part = mock(Part.class);
                when(part.getSubmittedFileName()).thenReturn(filename);
                onePartCollection.add(part);
            }
            Sequence sequence = new Sequence(onePartCollection);
            assertThat(sequence.isValid()).isEqualTo(true);
        }
    }
}
