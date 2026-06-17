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

class MissedCBetDetectorTest {

    private final MissedCBetDetector detector = new MissedCBetDetector();

    private HandHistory hand(String id, Map<Street, List<Action>> actions) {
        return new HandHistory(
                id, "Hold'em No Limit", 0.25, 0.50, null,
                "Test", 6, 1, List.of(), "Hero", List.of(),
                actions, List.of(), 0, 0
        );
    }

    @Test
    void flagsPfrCheckingFlop() {
        HandHistory h = hand("h1", Map.of(
                Street.PREFLOP, List.of(
                        new Action("sb", Street.PREFLOP, ActionType.POST_SMALL_BLIND, 0.25, 0, false),
                        new Action("bb", Street.PREFLOP, ActionType.POST_BIG_BLIND, 0.50, 0, false),
                        new Action("pfr", Street.PREFLOP, ActionType.RAISE, 1.0, 1.50, false),
                        new Action("bb", Street.PREFLOP, ActionType.CALL, 1.0, 0, false)
                ),
                Street.FLOP, List.of(
                        new Action("bb", Street.FLOP, ActionType.CHECK, 0, 0, false),
                        new Action("pfr", Street.FLOP, ActionType.CHECK, 0, 0, false)
                )
        ));
        List<Leak> leaks = detector.detect(h);
        assertEquals(1, leaks.size());
        assertEquals("pfr", leaks.get(0).player());
        assertEquals(LeakType.MISSED_C_BET, leaks.get(0).type());
        assertEquals(Street.FLOP, leaks.get(0).street());
    }

    @Test
    void noLeakWhenPfrBetsFlop() {
        HandHistory h = hand("h1", Map.of(
                Street.PREFLOP, List.of(
                        new Action("sb", Street.PREFLOP, ActionType.POST_SMALL_BLIND, 0.25, 0, false),
                        new Action("bb", Street.PREFLOP, ActionType.POST_BIG_BLIND, 0.50, 0, false),
                        new Action("pfr", Street.PREFLOP, ActionType.RAISE, 1.0, 1.50, false),
                        new Action("bb", Street.PREFLOP, ActionType.CALL, 1.0, 0, false)
                ),
                Street.FLOP, List.of(
                        new Action("bb", Street.FLOP, ActionType.CHECK, 0, 0, false),
                        new Action("pfr", Street.FLOP, ActionType.BET, 2.0, 0, false)
                )
        ));
        assertTrue(detector.detect(h).isEmpty());
    }

    @Test
    void noLeakWhenNoFlop() {
        HandHistory h = hand("h1", Map.of(
                Street.PREFLOP, List.of(
                        new Action("sb", Street.PREFLOP, ActionType.POST_SMALL_BLIND, 0.25, 0, false),
                        new Action("bb", Street.PREFLOP, ActionType.POST_BIG_BLIND, 0.50, 0, false),
                        new Action("pfr", Street.PREFLOP, ActionType.RAISE, 1.0, 1.50, false),
                        new Action("bb", Street.PREFLOP, ActionType.FOLD, 0, 0, false)
                )
        ));
        assertTrue(detector.detect(h).isEmpty());
    }

    @Test
    void noLeakWhenNoPreflopRaise() {
        HandHistory h = hand("h1", Map.of(
                Street.PREFLOP, List.of(
                        new Action("sb", Street.PREFLOP, ActionType.POST_SMALL_BLIND, 0.25, 0, false),
                        new Action("bb", Street.PREFLOP, ActionType.POST_BIG_BLIND, 0.50, 0, false),
                        new Action("limper", Street.PREFLOP, ActionType.CALL, 0.50, 0, false),
                        new Action("bb", Street.PREFLOP, ActionType.CHECK, 0, 0, false)
                ),
                Street.FLOP, List.of(
                        new Action("bb", Street.FLOP, ActionType.CHECK, 0, 0, false),
                        new Action("limper", Street.FLOP, ActionType.CHECK, 0, 0, false)
                )
        ));
        assertTrue(detector.detect(h).isEmpty(), "no PFR means no c-bet expected");
    }
}
