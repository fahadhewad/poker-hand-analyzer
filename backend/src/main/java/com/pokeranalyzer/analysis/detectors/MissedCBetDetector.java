package com.pokeranalyzer.analysis.detectors;

import com.pokeranalyzer.analysis.Leak;
import com.pokeranalyzer.analysis.LeakDetector;
import com.pokeranalyzer.analysis.LeakSeverity;
import com.pokeranalyzer.analysis.LeakType;
import com.pokeranalyzer.model.Action;
import com.pokeranalyzer.model.ActionType;
import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.model.Street;

import java.util.List;

public class MissedCBetDetector implements LeakDetector {

    @Override
    public List<Leak> detect(HandHistory hand) {
        String lastRaiser = null;
        for (Action a : hand.actions().getOrDefault(Street.PREFLOP, List.of())) {
            if (a.type() == ActionType.RAISE) {
                lastRaiser = a.player();
            }
        }
        if (lastRaiser == null) {
            return List.of();
        }

        List<Action> flop = hand.actions().getOrDefault(Street.FLOP, List.of());
        if (flop.isEmpty()) {
            return List.of();
        }

        for (Action a : flop) {
            if (a.player().equals(lastRaiser)) {
                if (a.type() == ActionType.CHECK) {
                    return List.of(new Leak(
                            lastRaiser,
                            LeakType.MISSED_C_BET,
                            LeakSeverity.MINOR,
                            hand.handId(),
                            Street.FLOP,
                            lastRaiser + " was the preflop aggressor but checked the flop "
                                    + "instead of c-betting."
                    ));
                }
                return List.of();
            }
        }
        return List.of();
    }
}
