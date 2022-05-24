package com.infront.assessment.provider;

import com.infront.assessment.config.AppConfig;
import com.infront.assessment.model.InstrumentShortingHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FinanstilsynetDataProvider {

    static final String GENERIC_ERROR_MESSAGE = "Unknown error occured, please try again later";

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    public List<InstrumentShortingHistory> getAllInstruments() {
        try {
            ResponseEntity<InstrumentShortingHistory[]> entity = restTemplate.getForEntity(appConfig.getFinanstilsynetUrl(), InstrumentShortingHistory[].class);
            if(isNull(entity.getBody())) {
                log.warn("Received null response from Finanstilsynet endpoint {}", appConfig.getFinanstilsynetUrl());
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return Arrays.asList(entity.getBody());
        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(ex.getStatusCode());
        } catch (Exception ex) {
            log.error(format("Unexpected error while calling %s", appConfig.getFinanstilsynetUrl()), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE);
        }
    }

}
