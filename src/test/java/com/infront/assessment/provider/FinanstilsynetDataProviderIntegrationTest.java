package com.infront.assessment.provider;

import com.infront.assessment.model.InstrumentShortingHistory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FinanstilsynetDataProviderIntegrationTest {

    @Autowired
    private FinanstilsynetDataProvider provider;

    @Disabled("Use this test to verify real response from Finanstilsynet")
    @Test
    void should_fetch_data_from_finanstilsynet() {
        List<InstrumentShortingHistory> allShortPositions = provider.getAllInstruments();
        assertThat(allShortPositions)
                .isNotEmpty();
    }

}