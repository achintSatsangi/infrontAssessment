package com.infront.assessment.controller;

import com.infront.assessment.ObjectProvider;
import com.infront.assessment.model.InstrumentShortingHistory;
import com.infront.assessment.model.Request;
import com.infront.assessment.service.InstrumentService;
import com.infront.assessment.validator.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InstrumentControllerTest {

    private InstrumentController controller;

    private InstrumentService instrumentServiceMock;
    private RequestValidator validatorMock;

    @BeforeEach
    void setup() {
        instrumentServiceMock = mock(InstrumentService.class);
        validatorMock = mock(RequestValidator.class);
        controller = new InstrumentController(instrumentServiceMock, validatorMock);
    }

    @Test
    void should_create_request_object_as_expected_and_call_validator_and_service() {
        InstrumentShortingHistory expected = ObjectProvider.getDummyInstruments().get(0);
        when(instrumentServiceMock.getShortingHistory(any(Request.class)))
                .thenReturn(expected);

        ResponseEntity<InstrumentShortingHistory> result
                = controller.getShortingHistory("1234", LocalDate.MIN, LocalDate.now());

        assertThat(result.getBody()).isEqualTo(expected);
        verify(validatorMock).validate(any(Request.class));
        verify(instrumentServiceMock).getShortingHistory(any(Request.class));
    }
}