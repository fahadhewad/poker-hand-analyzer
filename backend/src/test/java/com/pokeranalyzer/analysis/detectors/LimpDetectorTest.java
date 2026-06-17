package com.pokeranalyzer.analysis.detectors;

import com.pokeranalyzer.analysis.Leak;
import com.pokeranalyzer.analysis.LeakType;
import com.pokeranalyzer.model.Action;
import com.pokeranalyzer.model.ActionType;
import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.model.Street;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimpDetectorTest {

    private final LimpDetector detector = new LimpDetector();

    private HandHistory hand(String id, List<Action> preflop) {
        return new HandHistory(
                id, "Hold'em No Limit", 0.25, 0.50, null,
                "Test", 6, 1, List.of(), "Hero", List.of(),
                Map.of(Street.PREFLOP, preflop),
                List.of(), 0, 0
        );
    }

    @Test
    void flagsCallWhenNoRaiseYet() {
        HandHistory h = hand("h1", List.of(
                new Action("sb", Street.PREFLOP, ActionType.POST_SMALL_BLIND, 0.25, 0, false),
                new Action("bb", Street.PREFLOP, ActionType.POST_BIG_BLIND, 0.50, 0, false),
                new Action("limper", Street.PREFLOP, ActionType.CALL, 0.50, 0, false),
                new Action("nit", Street.PREFLOP, ActionType.FOLD, 0, 0, false)
        ));
        List<Leak> leaks = detector.detect(h);
        assertEquals(1, leaks.size());
        assertEquals("limper", leaks.get(0).player());
        assertEquals(LeakType.LIMP, leaks.get(0).type());
    }

    @Test
    void doesNotFlagCallAfterRaise() {
        HandHistory h = hand("h1", List.of(
                new Action("sb", Street.PREFLOP, ActionType.POST_SMALL_BLIND, 0.25, 0, false),
                new Action("bb", Street.PREFLOP, ActionType.POST_BIG_BLIND, 0.50, 0, false),
                new Action("raiser", Street.PREFLOP, ActionType.RAISE, 1.0, 1.50, false),
                new Action("caller", Street.PREFLOP, ActionType.CALL, 1.50, 0, false)
        ));
        assertTrue(detector.detect(h).isEmpty(), "calling a raise is not a limp");
    }

    @Test
    void flagsMultipleLimpers() {
        HandHistory h = hand("h1", List.of(
                new Action("sb", Street.PREFLOP, ActionType.POST_SMALL_BLIND, 0.25, 0, false),
                new Action("bb", Street.PREFLOP, ActionType.POST_BIG_BLIND, 0.50, 0, false),
                new Action("limper1", Street.PREFLOP, ActionType.CALL, 0.50, 0, false),
                new Action("limper2", Street.PREFLOP, ActionType.CALL, 0.50, 0, false)
        ));
        assertEquals(2, detector.detect(h).size());
    }
}
