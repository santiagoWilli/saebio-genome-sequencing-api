package unit.handlers;

import dataaccess.DataAccess;
import handlers.references.ReferencesGetAllHandler;
import handlers.reports.ReportsGetAllHandler;
import handlers.sequences.SequencesGetAllHandler;
import handlers.strains.StrainsGetAllHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GetAllHandler_ {
    private DataAccess dataAccess;
    private RequestParams requestParams;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        requestParams = mock(RequestParams.class);
        when(requestParams.query()).thenReturn(new HashMap<>());
    }

    @Test
    public void alwaysReturnHttp200_and_callDataAccessMethodOnce_forSequences() {
        when(dataAccess.getAllSequences()).thenReturn("abc");
        assertThat(new SequencesGetAllHandler(dataAccess).process(new EmptyPayload(), requestParams))
                .isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getAllSequences();
    }

    @Test
    public void alwaysReturnHttp200_and_callDataAccessMethodOnce_forReferences() {
        when(dataAccess.getAllReferences()).thenReturn("abc");
        assertThat(new ReferencesGetAllHandler(dataAccess).process(new EmptyPayload(), requestParams))
                .isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getAllReferences();
    }

    @Test
    public void alwaysReturnHttp200_and_callDataAccessMethodOnce_forReports() {
        when(dataAccess.getAllReports()).thenReturn("abc");
        assertThat(new ReportsGetAllHandler(dataAccess).process(new EmptyPayload(), requestParams))
                .isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getAllReports();
    }

    @Test
    public void alwaysReturnHttp200_and_callDataAccessMethodOnce_forStrains() {
        when(dataAccess.getAllStrains()).thenReturn("abc");
        assertThat(new StrainsGetAllHandler(dataAccess).process(new EmptyPayload(), requestParams))
                .isEqualTo(new Answer(200, "abc"));
        verify(dataAccess, times(1)).getAllStrains();
    }
}
