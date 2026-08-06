package com.pokeranalyzer.web.dto;

import com.pokeranalyzer.analysis.Leak;
import com.pokeranalyzer.analysis.PlayerStats;
import com.pokeranalyzer.analysis.PlayerType;

import java.util.List;

public record PlayerReport(
        String name,
        int handsPlayed,
        double vpip,
        double pfr,
        double threeBetPct,
        Double aggressionFactor,
        int postflopBets,
        int postflopRaises,
        int postflopCalls,
        PlayerType type,
        List<Leak> leaks
) {

    public static PlayerReport from(PlayerStats stats, PlayerType type, List<Leak> leaks) {
        double af = stats.aggressionFactor();
        Double safeAf = Double.isFinite(af) ? af : null;
        return new PlayerReport(
                stats.player(),
                stats.handsPlayed(),
                stats.vpip(),
                stats.pfr(),
                stats.threeBetPct(),
                safeAf,
                stats.postflopBets(),
                stats.postflopRaises(),
                stats.postflopCalls(),
                type,
                leaks
        );
    }
}
