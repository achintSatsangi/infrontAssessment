package com.infront.assessment.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class Request {

    @NonNull
    String isin;

    @Builder.Default
    LocalDate fromDate = LocalDate.MIN;

    @Builder.Default
    LocalDate toDate = LocalDate.now();
}
