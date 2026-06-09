package com.pokeranalyzer.analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerClassifierTest {

    private final PlayerClassifier classifier = new PlayerClassifier();

    private PlayerStats stats(int handsPlayed, int vpip, int pfr,
                              int postflopBets, int postflopRaises, int postflopCalls) {
        return new PlayerStats(
                "p", handsPlayed,
                vpip, pfr,
                0, 0,
                postflopBets, postflopRaises, postflopCalls
        );
    }

    @Test
    void zeroHandsIsUnclassified() {
        assertEquals(PlayerType.UNCLASSIFIED, classifier.classify(stats(0, 0, 0, 0, 0, 0)));
    }

    @Test
    void tightWithLowAggressionIsNit() {
        PlayerStats s = stats(100, 10, 8, 3, 0, 2);
        assertEquals(PlayerType.NIT, classifier.classify(s));
    }

    @Test
    void balancedTightAggressiveIsTag() {
        PlayerStats s = stats(100, 22, 18, 3, 2, 2);
        assertEquals(PlayerType.TAG, classifier.classify(s));
    }

    @Test
    void looseAggressiveIsLag() {
        PlayerStats s = stats(100, 30, 24, 4, 2, 2);
        assertEquals(PlayerType.LAG, classifier.classify(s));
    }

    @Test
    void veryLooseAndVeryAggressiveIsManiac() {
        PlayerStats s = stats(100, 50, 40, 7, 3, 2);
        assertEquals(PlayerType.MANIAC, classifier.classify(s));
    }

    @Test
    void highVpipLowPfrLowAfIsCallingStation() {
        PlayerStats s = stats(100, 40, 5, 1, 0, 5);
        assertEquals(PlayerType.CALLING_STATION, classifier.classify(s));
    }

    @Test
    void moderatelyLooseAndPassiveIsLoosePassive() {
        PlayerStats s = stats(100, 28, 10, 2, 0, 2);
        assertEquals(PlayerType.LOOSE_PASSIVE, classifier.classify(s));
    }

    @Test
    void infiniteAggressionFactorClassifiesAsAggressive() {
        PlayerStats s = stats(100, 30, 24, 5, 2, 0);
        assertEquals(PlayerType.LAG, classifier.classify(s));
    }
}
