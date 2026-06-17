package com.pokeranalyzer.analysis.detectors;

import com.pokeranalyzer.analysis.Leak;
import com.pokeranalyzer.analysis.LeakDetector;
import com.pokeranalyzer.analysis.LeakSeverity;
import com.pokeranalyzer.analysis.LeakType;
import com.pokeranalyzer.model.Action;
import com.pokeranalyzer.model.ActionType;
import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.model.Street;

import java.util.ArrayList;
import java.util.List;

public class LimpDetector implements LeakDetector {

    @Override
    public List<Leak> detect(HandHistory hand) {
        List<Leak> leaks = new ArrayList<>();
        int raiseCount = 0;
        for (Action a : hand.actions().getOrDefault(Street.PREFLOP, List.of())) {
            switch (a.type()) {
                case CALL -> {
                    if (raiseCount == 0) {
                        leaks.add(new Leak(
                                a.player(),
                                LeakType.LIMP,
                                LeakSeverity.MINOR,
                                hand.handId(),
                                Street.PREFLOP,
                                a.player() + " limped preflop (called $"
                                        + a.amount() + " with no raise yet). "
                                        + "Consider raising or folding instead."
                        ));
                    }
                }
                case RAISE -> raiseCount++;
                default -> {
                }
            }
        }
        return leaks;
    }
}
