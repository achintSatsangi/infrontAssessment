package com.infront.assessment.validator;

import com.infront.assessment.model.Request;
import org.apache.commons.validator.routines.ISINValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestValidator {

    public void validate(Request request) {
        List<String> errorMessages = new ArrayList<>();
        if(!ISINValidator.getInstance(true).isValid(request.getIsin())) {
            errorMessages.add("Invalid ISIN format");
        }
        if(request.getFromDate().isAfter(LocalDate.now())) {
            errorMessages.add("fromDate is in the future");
        }
        if(request.getToDate().isAfter(LocalDate.now())) {
            errorMessages.add("ToDate is in the future");
        }
        if(request.getFromDate().isAfter(request.getToDate())) {
            errorMessages.add("fromDate is greater than toDate");
        }
        if(!errorMessages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errorMessages), new IllegalArgumentException());
        }
    }
}
