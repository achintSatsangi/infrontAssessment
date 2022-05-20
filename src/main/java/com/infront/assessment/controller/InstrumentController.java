package com.infront.assessment.controller;

import com.infront.assessment.model.InstrumentShortingHistory;
import com.infront.assessment.model.Request;
import com.infront.assessment.service.InstrumentService;
import com.infront.assessment.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@RestController
@RequiredArgsConstructor
public class InstrumentController implements InstrumentsApi {

    private final InstrumentService instrumentService;
    private final RequestValidator validator;

    @Override
    public ResponseEntity<InstrumentShortingHistory>
        getShortingHistory(String isin, LocalDate fromDate, LocalDate toDate) {
        Request request = Request.builder()
                .isin(isin)
                .fromDate(isNull(fromDate) ? LocalDate.MIN : fromDate)
                .toDate(isNull(toDate) ? LocalDate.now() : toDate)
                .build();
        validator.validate(request);
        return ResponseEntity.ok(this.instrumentService.getShortingHistory(request));
    }
}
