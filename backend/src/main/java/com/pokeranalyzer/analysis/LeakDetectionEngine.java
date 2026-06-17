package com.pokeranalyzer.analysis;

import com.pokeranalyzer.analysis.detectors.LimpDetector;
import com.pokeranalyzer.analysis.detectors.MissedCBetDetector;
import com.pokeranalyzer.model.HandHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeakDetectionEngine {

    private final List<LeakDetector> detectors;

    public LeakDetectionEngine() {
        this(List.of(new LimpDetector(), new MissedCBetDetector()));
    }

    public LeakDetectionEngine(List<LeakDetector> detectors) {
        this.detectors = List.copyOf(detectors);
    }

    public Map<String, List<Leak>> detect(List<HandHistory> hands) {
        Map<String, List<Leak>> out = new HashMap<>();
        for (HandHistory hand : hands) {
            for (LeakDetector d : detectors) {
                for (Leak leak : d.detect(hand)) {
                    out.computeIfAbsent(leak.player(), k -> new ArrayList<>()).add(leak);
                }
            }
        }
        Map<String, List<Leak>> immutable = new HashMap<>();
        out.forEach((p, leaks) -> immutable.put(p, List.copyOf(leaks)));
        return Map.copyOf(immutable);
    }
}
