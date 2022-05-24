package com.infront.assessment.validator;

import com.infront.assessment.model.Request;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestValidatorTest {

    RequestValidator validator = new RequestValidator();
    
    @Test
    void should_validate_invalid_ISIN() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> validator.validate(Request.builder()
                .isin("1234")
                .build()));

        assertThat(exception).extracting("status", "reason")
                .containsExactly(HttpStatus.BAD_REQUEST, "Invalid ISIN format");
    }

    @Test
    void should_validate_date_inputs_in_future() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> validator.validate(Request.builder()
                .isin("BMG9156K1018")
                .fromDate(LocalDate.now().plusDays(2))
                .toDate(LocalDate.now().plusDays(2))
                .build()));

        assertThat(exception).extracting("status", "reason")
                .containsExactly(HttpStatus.BAD_REQUEST, "fromDate is in the future, ToDate is in the future");
    }

    @Test
    void should_validate_from_date_greater_than_to_date() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> validator.validate(Request.builder()
                    .isin("BMG9156K1018")
                    .fromDate(LocalDate.now().minusDays(1))
                    .toDate(LocalDate.now().minusDays(2))
                    .build()));

        assertThat(exception).extracting("status", "reason")
                .containsExactly(HttpStatus.BAD_REQUEST, "fromDate is greater than toDate");
    }

    @Test
    void should_have_all_errors_for_invalid_request() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> validator.validate(Request.builder()
                    .isin("1234")
                    .fromDate(LocalDate.now().plusDays(10))
                    .toDate(LocalDate.now().plusDays(2))
                    .build()));

        assertThat(exception).extracting("status", "reason")
                .containsExactly(HttpStatus.BAD_REQUEST, "Invalid ISIN format, " +
                        "fromDate is in the future, " +
                        "ToDate is in the future, " +
                        "fromDate is greater than toDate");
    }
}