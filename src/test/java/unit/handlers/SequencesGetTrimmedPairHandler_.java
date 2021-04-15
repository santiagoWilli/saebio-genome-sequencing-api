package unit.handlers;

import dataaccess.DataAccess;
import handlers.SequencesGetTrimmedPairHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SequencesGetTrimmedPairHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private SequencesGetTrimmedPairHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new SequencesGetTrimmedPairHandler(dataAccess);
    }

    @Test
    public void ifSequenceNotFound_returnHttpNotFound() {
        when(dataAccess.getSequence(PARAMS.get(":id"))).thenReturn("");
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(Answer.notFound());
    }

    @Test
    public void ifSequenceFound_and_hasItsTrimmedPair_returnHttpOk_and_file() throws FileNotFoundException {
        String trimmedId1 = "6075d6a71a62381d13c70a6f";
        String trimmedId2 = "6075d6aa1a62381d13c70c9b";
        when(dataAccess.getSequence(PARAMS.get(":id"))).thenReturn("{\"_id\": {\"$oid\": \"1\"}, \"trimmedPair\": [{\"$oid\": \""+trimmedId1+"\"}, {\"$oid\": \""+trimmedId2+"\"}]}");
        when(dataAccess.getTrimmedFileName(trimmedId1)).thenReturn("Kp1_R1_trimmed.fq.gz");
        when(dataAccess.getTrimmedFileName(trimmedId2)).thenReturn("Kp1_R2_trimmed.fq.gz");
        when(dataAccess.getTrimmedFileStream(trimmedId1)).thenReturn(new FileInputStream("test/resources/sequences/Kp1_231120_R1.fastq.gz"));
        when(dataAccess.getTrimmedFileStream(trimmedId2)).thenReturn(new FileInputStream("test/resources/sequences/Kp1_231120_R1.fastq.gz"));

        Answer answer = handler.process(new EmptyPayload(), PARAMS);
        assertThat(answer.getCode()).isEqualTo(200);
        assertThat(answer.hasFile()).isTrue();
        assertThat(answer.getFile().getMimeType()).isEqualTo("application/zip");
    }

    @Test
    public void ifSequenceFound_and_doesNotHaveItsTrimmedPairYet_returnHttp210_and_notice() {
        when(dataAccess.getSequence(PARAMS.get(":id"))).thenReturn("{\"_id\": {\"$oid\": \"1\"}}");

        Answer answer = handler.process(new EmptyPayload(), PARAMS);
        assertThat(answer.getCode()).isEqualTo(210);
        assertThat(answer.hasFile()).isFalse();
        assertThat(answer.getBody()).contains("The sequence does not have its trimmed pair yet");
    }

    @Test
    public void ifSequenceFound_and_trimmedPairIsSetToFalse_returnHttp211_and_notice() {
        when(dataAccess.getSequence(PARAMS.get(":id"))).thenReturn("{\"_id\": {\"$oid\": \"1\"}, \"trimmedPair\": false}");

        Answer answer = handler.process(new EmptyPayload(), PARAMS);
        assertThat(answer.getCode()).isEqualTo(211);
        assertThat(answer.hasFile()).isFalse();
        assertThat(answer.getBody()).contains("The sequence does not have a trimmed pair due to an internal error");
    }
}
