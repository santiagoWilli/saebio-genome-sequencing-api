package unit.handlers;

import dataaccess.DataAccess;
import handlers.ReferencesGetOneHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReferencesGetOneHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private ReferencesGetOneHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new ReferencesGetOneHandler(dataAccess);
    }

    @Test
    public void ifReferenceNotFound_returnHttpNotFound() {
        when(dataAccess.getReference(PARAMS.get(":id"))).thenReturn("");
        assertThat(handler.process(new EmptyPayload(), PARAMS)).isEqualTo(Answer.notFound());
    }

    @Test
    public void ifReferenceFound_returnHttpOk_and_file() throws IOException {
        String fileId = "6075d6a71a62381d13c70a6f";
        when(dataAccess.getReference(PARAMS.get(":id"))).thenReturn("{\"_id\": {\"$oid\": \"1\"}, \"file\": {\"$oid\": \""+fileId+"\"}}");
        when(dataAccess.getFileStream(fileId)).thenReturn(new FileInputStream("test/resources/sequences/Kpneu231120_referencia.fa"));

        Answer answer = handler.process(new EmptyPayload(), PARAMS);
        assertThat(answer.getCode()).isEqualTo(200);
        assertThat(answer.hasFile()).isTrue();
        assertThat(answer.getFile().getMimeType()).isEqualTo("text/x-fasta");
    }
}
