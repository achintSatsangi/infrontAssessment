package com.infront.assessment.provider;

import com.infront.assessment.config.AppConfig;
import com.infront.assessment.model.InstrumentShortingHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.infront.assessment.provider.FinanstilsynetDataProvider.GENERIC_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class FinanstilsynetDataProviderTest {

    private FinanstilsynetDataProvider provider;

    private RestTemplate restTemplateMock;
    private AppConfig appConfigMock;

    @BeforeEach
    void setup() {
        restTemplateMock = mock(RestTemplate.class);
        appConfigMock = mock(AppConfig.class);
        when(appConfigMock.getFinanstilsynetUrl()).thenReturn("http://localhost:8080");
        provider = new FinanstilsynetDataProvider(restTemplateMock, appConfigMock);
    }

    @Test
    void should_throw_exception_if_finanstilsynet_returns_null_body() {
        when(restTemplateMock.getForEntity(appConfigMock.getFinanstilsynetUrl(), InstrumentShortingHistory[].class))
                .thenReturn(ResponseEntity.ok().build());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> provider.getAllInstruments());

        assertThat(exception).extracting("status", "reason")
                .containsExactly(INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE);
    }

    @Test
    void should_throw_client_exception_if_finanstilsynet_throws_client_exception() {
        when(restTemplateMock.getForEntity(appConfigMock.getFinanstilsynetUrl(), InstrumentShortingHistory[].class))
                .thenThrow(new HttpClientErrorException(BAD_REQUEST, "Bad Input"));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> provider.getAllInstruments());

        assertThat(exception.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void should_throw_generic_server_exception_if_finanstilsynet_throws_server_exception() {
        when(restTemplateMock.getForEntity(appConfigMock.getFinanstilsynetUrl(), InstrumentShortingHistory[].class))
                .thenThrow(new HttpServerErrorException(BAD_GATEWAY, "Bad gateway"));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> provider.getAllInstruments());

        assertThat(exception).extracting("status", "reason")
                .containsExactly(INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE);
    }

    @Test
    void should_call_rest_template_and_return_response() {
        InstrumentShortingHistory instrumentShortingHistory = new InstrumentShortingHistory();
        instrumentShortingHistory.setIsin("1234");
        InstrumentShortingHistory[] instrumentShortingHistories = new InstrumentShortingHistory[1];
        instrumentShortingHistories[0] = instrumentShortingHistory;
        ResponseEntity<InstrumentShortingHistory[]> mockResponse = ResponseEntity.ok(instrumentShortingHistories);
        when(restTemplateMock.getForEntity(appConfigMock.getFinanstilsynetUrl(), InstrumentShortingHistory[].class))
                .thenReturn(mockResponse);

        List<InstrumentShortingHistory> result = provider.getAllInstruments();

        assertThat(result).hasSize(1)
                        .containsExactly(instrumentShortingHistory);
        verify(restTemplateMock).getForEntity(appConfigMock.getFinanstilsynetUrl(), InstrumentShortingHistory[].class);
    }

}