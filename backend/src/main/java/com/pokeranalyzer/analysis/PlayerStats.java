package com.pokeranalyzer.analysis;

public record PlayerStats(
        String player,
        int handsPlayed,
        int vpipCount,
        int pfrCount,
        int threeBetCount,
        int threeBetOpportunities,
        int postflopBets,
        int postflopRaises,
        int postflopCalls
) {

    public double vpip() {
        return handsPlayed == 0 ? 0.0 : (double) vpipCount / handsPlayed;
    }

    public double pfr() {
        return handsPlayed == 0 ? 0.0 : (double) pfrCount / handsPlayed;
    }

    public double threeBetPct() {
        return threeBetOpportunities == 0 ? 0.0 : (double) threeBetCount / threeBetOpportunities;
    }

    public double aggressionFactor() {
        if (postflopCalls == 0) {
            return postflopBets + postflopRaises == 0 ? 0.0 : Double.POSITIVE_INFINITY;
        }
        return (double) (postflopBets + postflopRaises) / postflopCalls;
    }
}
