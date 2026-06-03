package com.pokeranalyzer.analysis;

import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.parser.HandHistoryParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StatsCalculatorTest {

    private final HandHistoryParser parser = new HandHistoryParser();
    private final StatsCalculator calculator = new StatsCalculator();

    private List<HandHistory> sampleHands() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/cash-6max.txt")) {
            assertNotNull(in, "cash-6max.txt fixture missing");
            String raw = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return parser.parseAll(raw);
        }
    }

    @Test
    void countsHandsPlayedPerSeatedPlayer() throws IOException {
        Map<String, PlayerStats> stats = calculator.calculate(sampleHands());
        assertEquals(2, stats.get("Hero").handsPlayed());
        assertEquals(2, stats.get("villain1").handsPlayed());
        assertEquals(2, stats.get("villain2").handsPlayed());
    }

    @Test
    void heroVpipsOnceAndNeverRaises() throws IOException {
        PlayerStats hero = calculator.calculate(sampleHands()).get("Hero");
        assertEquals(1, hero.vpipCount());
        assertEquals(0, hero.pfrCount());
        assertEquals(0.5, hero.vpip(), 0.0001);
        assertEquals(0.0, hero.pfr(), 0.0001);
    }

    @Test
    void openRaiserIsPfrButNotThreeBet() throws IOException {
        PlayerStats v2 = calculator.calculate(sampleHands()).get("villain2");
        assertEquals(1, v2.pfrCount());
        assertEquals(0, v2.threeBetCount());
        assertEquals(0, v2.threeBetOpportunities(),
                "villain2 was first to enter the pot when they entered");
    }

    @Test
    void heroFacedThreeBetOpportunityInBothHands() throws IOException {
        PlayerStats hero = calculator.calculate(sampleHands()).get("Hero");
        assertEquals(2, hero.threeBetOpportunities());
        assertEquals(0, hero.threeBetCount());
        assertEquals(0.0, hero.threeBetPct(), 0.0001);
    }

    @Test
    void postflopAggressionForHero() throws IOException {
        PlayerStats hero = calculator.calculate(sampleHands()).get("Hero");
        assertEquals(1, hero.postflopBets());
        assertEquals(0, hero.postflopRaises());
        assertEquals(1, hero.postflopCalls());
        assertEquals(1.0, hero.aggressionFactor(), 0.0001);
    }

    @Test
    void foldingBlindsRegisterThreeBetOpportunity() throws IOException {
        Map<String, PlayerStats> stats = calculator.calculate(sampleHands());
        PlayerStats v5 = stats.get("villain5");
        PlayerStats v6 = stats.get("villain6");
        assertEquals(2, v5.threeBetOpportunities());
        assertEquals(2, v6.threeBetOpportunities());
        assertEquals(0, v5.vpipCount());
        assertEquals(0, v6.vpipCount());
    }

    @Test
    void bbCallingARaiseCountsAsVpipAndThreeBetOpp() throws IOException {
        PlayerStats v1 = calculator.calculate(sampleHands()).get("villain1");
        assertEquals(1, v1.vpipCount(), "villain1 called villain3's raise as BB in hand 2");
        assertEquals(1, v1.threeBetOpportunities());
        assertEquals(0, v1.pfrCount());
    }
}
