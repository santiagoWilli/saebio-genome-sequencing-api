package unit;

import org.junit.jupiter.api.Test;
import payloads.Sequence;

import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class Sequence_ {
    @Test
    public void invalid_if_onlyOnePart() {
        Part part = mock(Part.class);
        Collection<Part> onePartCollection = new ArrayList<>();
        onePartCollection.add(part);
        Sequence sequence = new Sequence(onePartCollection);
        assertThat(sequence.isValid()).isEqualTo(false);
    }
}
