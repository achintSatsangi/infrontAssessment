package com.infront.assessment.service;

import com.infront.assessment.model.InstrumentShortingHistory;
import com.infront.assessment.model.Request;
import com.infront.assessment.provider.FinanstilsynetDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static com.infront.assessment.ObjectProvider.getDummyInstruments;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class InstrumentServiceTest {

    private InstrumentService instrumentService;

    private FinanstilsynetDataProvider finanstilsynetDataProviderMock;

    @BeforeEach
    void setup() {
        finanstilsynetDataProviderMock = mock(FinanstilsynetDataProvider.class);
        instrumentService = new InstrumentService(finanstilsynetDataProviderMock);
    }

    @Test
    void should_propagate_exception_thrown_by_provider() {
        RuntimeException somethingBad = new RuntimeException("Something bad");
        when(finanstilsynetDataProviderMock.getAllInstruments()).thenThrow(somethingBad);

        RuntimeException runtimeException =
                assertThrows(RuntimeException.class, () ->
                        instrumentService.getShortingHistory(Request.builder()
                                .isin("1234")
                                .build()));

        assertThat(runtimeException).isEqualTo(somethingBad);
    }

    @Test
    void should_throw_not_found_if_isin_not_found() {
        when(finanstilsynetDataProviderMock.getAllInstruments()).thenReturn(getDummyInstruments());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () ->
                        instrumentService.getShortingHistory(Request.builder()
                                .isin("12345678")
                                .build()));

        assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
    }

    @Test
    void should_return_matching_instrument_short_history() {
        List<InstrumentShortingHistory> dummyInstruments = getDummyInstruments();
        when(finanstilsynetDataProviderMock.getAllInstruments()).thenReturn(dummyInstruments);

        InstrumentShortingHistory result = instrumentService.getShortingHistory(Request.builder()
                .isin(dummyInstruments.get(0).getIsin())
                .build());

        assertThat(result).isEqualTo(dummyInstruments.get(0));
    }

    @Test
    void should_return_matching_instrument_short_history_with_filtered_events() {
        List<InstrumentShortingHistory> dummyInstruments = getDummyInstruments();
        when(finanstilsynetDataProviderMock.getAllInstruments()).thenReturn(dummyInstruments);

        InstrumentShortingHistory result = instrumentService.getShortingHistory(Request.builder()
                .isin(dummyInstruments.get(0).getIsin())
                .fromDate(LocalDate.now().minusDays(3))
                .toDate(LocalDate.now())
                .build());

        InstrumentShortingHistory expected = dummyInstruments.get(0);
        assertThat(result).extracting("isin", "issuerName")
                .containsExactly(expected.getIsin(), expected.getIssuerName());
        assertThat(result.getEvents()).hasSize(1)
                .extracting("date")
                .containsExactly(expected.getEvents().get(0).getDate());
    }

    @Test
    void should_return_matching_instrument_short_history_with_filtered_events_boundary_conditions() {
        List<InstrumentShortingHistory> dummyInstruments = getDummyInstruments();
        when(finanstilsynetDataProviderMock.getAllInstruments()).thenReturn(dummyInstruments);

        InstrumentShortingHistory result = instrumentService.getShortingHistory(Request.builder()
                .isin(dummyInstruments.get(0).getIsin())
                .fromDate(LocalDate.now().minusDays(20))
                .toDate(LocalDate.now().minusDays(2))
                .build());

        assertThat(result).isEqualTo(dummyInstruments.get(0));
    }
}