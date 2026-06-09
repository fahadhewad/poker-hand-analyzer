package com.pokeranalyzer.analysis;

public class PlayerClassifier {

    public PlayerType classify(PlayerStats stats) {
        if (stats.handsPlayed() == 0) {
            return PlayerType.UNCLASSIFIED;
        }

        double vpip = stats.vpip();
        double pfr = stats.pfr();
        double af = stats.aggressionFactor();
        double effectiveAf = Double.isFinite(af) ? af : 4.0;

        if (vpip >= 0.35 && pfr >= 0.30 && effectiveAf >= 3.0) {
            return PlayerType.MANIAC;
        }
        if (vpip >= 0.27 && pfr >= 0.18 && effectiveAf >= 2.0) {
            return PlayerType.LAG;
        }
        if (vpip >= 0.18 && vpip <= 0.26 && pfr >= vpip - 0.07 && effectiveAf >= 1.5) {
            return PlayerType.TAG;
        }
        if (vpip < 0.15) {
            return PlayerType.NIT;
        }
        if (vpip >= 0.25 && pfr < 0.12 && effectiveAf < 1.0) {
            return PlayerType.CALLING_STATION;
        }
        if (vpip >= 0.22 && pfr < 0.15) {
            return PlayerType.LOOSE_PASSIVE;
        }
        return PlayerType.UNCLASSIFIED;
    }
}
