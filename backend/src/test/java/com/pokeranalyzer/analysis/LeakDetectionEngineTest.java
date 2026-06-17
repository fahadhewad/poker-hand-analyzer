package com.pokeranalyzer.analysis;

import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.parser.HandHistoryParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeakDetectionEngineTest {

    private final HandHistoryParser parser = new HandHistoryParser();
    private final LeakDetectionEngine engine = new LeakDetectionEngine();

    private List<HandHistory> sampleHands() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/cash-6max.txt")) {
            assertNotNull(in, "cash-6max.txt fixture missing");
            String raw = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return parser.parseAll(raw);
        }
    }

    @Test
    void fixtureHandsHaveNoLimpsOrMissedCBets() throws IOException {
        Map<String, List<Leak>> leaks = engine.detect(sampleHands());
        assertTrue(leaks.isEmpty(),
                "fixture: no limps and both preflop raisers c-bet their flops");
    }
}
