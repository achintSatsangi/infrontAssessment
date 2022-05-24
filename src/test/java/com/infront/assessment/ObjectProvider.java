package com.infront.assessment;

import com.infront.assessment.model.AggregatedShortEvent;
import com.infront.assessment.model.InstrumentShortingHistory;

import java.time.LocalDate;
import java.util.List;

public class ObjectProvider {

    public static List<InstrumentShortingHistory> getDummyInstruments() {
        return List.of(
                new InstrumentShortingHistory()
                        .isin("BMG9156K1018")
                        .issuerName("2020 BULKERS")
                        .addEventsItem(new AggregatedShortEvent().date(LocalDate.now().minusDays(2)))
                        .addEventsItem(new AggregatedShortEvent().date(LocalDate.now().minusDays(20))),
                new InstrumentShortingHistory()
                        .isin("DK0060945467")
                        .issuerName("5TH PLANET GAMES")
                        .addEventsItem(new AggregatedShortEvent().date(LocalDate.now().minusDays(4))));
    }
}
