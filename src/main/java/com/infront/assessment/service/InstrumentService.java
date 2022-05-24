package com.infront.assessment.service;

import com.infront.assessment.model.AggregatedShortEvent;
import com.infront.assessment.model.InstrumentShortingHistory;
import com.infront.assessment.model.Request;
import com.infront.assessment.provider.FinanstilsynetDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final FinanstilsynetDataProvider finanstilsynetDataProvider;

    public InstrumentShortingHistory getShortingHistory(Request request) {
        return finanstilsynetDataProvider.getAllInstruments().stream()
                .filter(inst -> inst.getIsin().equals(request.getIsin()))
                .map(instrumentShortingHistory -> filterEventsByDates(instrumentShortingHistory, request))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private InstrumentShortingHistory filterEventsByDates(InstrumentShortingHistory instrumentShortingHistory, Request request) {
        return new InstrumentShortingHistory()
                .isin(instrumentShortingHistory.getIsin())
                .issuerName(instrumentShortingHistory.getIssuerName())
                .events(instrumentShortingHistory.getEvents().stream()
                        .filter(event -> filterEvent(request, event))
                        .collect(Collectors.toList()));
    }

    private boolean filterEvent(Request request, AggregatedShortEvent event) {
        return !(event.getDate().isBefore(request.getFromDate()) || event.getDate().isAfter(request.getToDate()));
    }
}
