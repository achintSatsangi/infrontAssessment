package com.infront.assessment;

import com.infront.assessment.model.InstrumentShortingHistory;
import com.infront.assessment.provider.FinanstilsynetDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static com.infront.assessment.ObjectProvider.getDummyInstruments;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    static List<InstrumentShortingHistory> dummyInstruments = getDummyInstruments();


    // Remove this bean to test the application against the real Finanstilsynet APIs
    @Bean
    @Primary
    public FinanstilsynetDataProvider dummyFinanstilsynetDataProvider() {
        FinanstilsynetDataProvider mock = mock(FinanstilsynetDataProvider.class);
        when(mock.getAllInstruments()).thenReturn(dummyInstruments);
        return mock;
    }

    private String applicationUrl(String servletPath) {
        return "http://localhost:" + port + "/" + servletPath;
    }

    @Test
    void should_throw_400_for_invalid_ISIN_format() {
        String requestPath = "/instruments/1234";
        ResponseEntity<String> result = testRestTemplate.getForEntity(applicationUrl(requestPath), String.class);
        assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void should_throw_404_for_no_ISIN() {
        String requestPath = "/instruments";
        ResponseEntity<String> result = testRestTemplate.getForEntity(applicationUrl(requestPath), String.class);
        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void should_throw_404_for_wrong_path() {
        String requestPath = "/instr____uments/1234";
        ResponseEntity<String> result = testRestTemplate.getForEntity(applicationUrl(requestPath), String.class);
        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void should_throw_400_for_invalid_input_with_error_message() {
        String requestPath = "/instruments/1234?fromDate=" + LocalDate.now().plusDays(2) + "&toDate=" + LocalDate.now().plusDays(1);
        ResponseEntity<String> result = testRestTemplate.getForEntity(applicationUrl(requestPath), String.class);
        assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);

        assertThat(result.getBody()).contains("Invalid ISIN format, " +
                "fromDate is in the future, " +
                "ToDate is in the future, " +
                "fromDate is greater than toDate");
    }

    @Test
    void should_throw_404_for_isin_that_does_not_exist_in_finanstilsynet() {
        String requestPath = "/instruments/EZ0000000003";
        ResponseEntity<String> result = testRestTemplate.getForEntity(applicationUrl(requestPath), String.class);
        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);

        assertThat(result.getBody()).contains(NOT_FOUND.getReasonPhrase());
    }

    @Test
    void should_return_200_with_matching_instrument_short_history() {
        String requestPath = "/instruments/BMG9156K1018";
        ResponseEntity<String> result = testRestTemplate.getForEntity(applicationUrl(requestPath), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);

        assertThat(result.getBody()).contains(dummyInstruments.get(0).getIssuerName(), dummyInstruments.get(0).getIsin());
    }
}
