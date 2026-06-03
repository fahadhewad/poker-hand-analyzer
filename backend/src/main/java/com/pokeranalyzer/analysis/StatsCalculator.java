package com.pokeranalyzer.analysis;

import com.pokeranalyzer.model.Action;
import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.model.Seat;
import com.pokeranalyzer.model.Street;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatsCalculator {

    public Map<String, PlayerStats> calculate(List<HandHistory> hands) {
        Map<String, Accumulator> accs = new HashMap<>();

        for (HandHistory hand : hands) {
            for (Seat seat : hand.seats()) {
                accs.computeIfAbsent(seat.player(), Accumulator::new).handsPlayed++;
            }
            accumulatePreflop(hand, accs);
            accumulatePostflop(hand, accs);
        }

        Map<String, PlayerStats> out = new HashMap<>();
        accs.forEach((player, a) -> out.put(player, a.toStats()));
        return Map.copyOf(out);
    }

    private void accumulatePreflop(HandHistory hand, Map<String, Accumulator> accs) {
        List<Action> preflop = hand.actions().getOrDefault(Street.PREFLOP, List.of());
        int raiseCount = 0;
        Set<String> vpip = new HashSet<>();
        Set<String> pfr = new HashSet<>();
        Set<String> threeBet = new HashSet<>();
        Set<String> threeBetOpp = new HashSet<>();

        for (Action a : preflop) {
            String p = a.player();
            switch (a.type()) {
                case POST_SMALL_BLIND, POST_BIG_BLIND, POST_ANTE -> {
                }
                case FOLD -> {
                    if (raiseCount >= 1) threeBetOpp.add(p);
                }
                case CHECK -> {
                }
                case CALL -> {
                    vpip.add(p);
                    if (raiseCount >= 1) threeBetOpp.add(p);
                }
                case BET -> {
                }
                case RAISE -> {
                    vpip.add(p);
                    pfr.add(p);
                    if (raiseCount >= 1) {
                        threeBet.add(p);
                        threeBetOpp.add(p);
                    }
                    raiseCount++;
                }
            }
        }

        for (String p : vpip) accs.computeIfAbsent(p, Accumulator::new).vpipCount++;
        for (String p : pfr) accs.computeIfAbsent(p, Accumulator::new).pfrCount++;
        for (String p : threeBet) accs.computeIfAbsent(p, Accumulator::new).threeBetCount++;
        for (String p : threeBetOpp) accs.computeIfAbsent(p, Accumulator::new).threeBetOpportunities++;
    }

    private void accumulatePostflop(HandHistory hand, Map<String, Accumulator> accs) {
        for (Street s : List.of(Street.FLOP, Street.TURN, Street.RIVER)) {
            for (Action a : hand.actions().getOrDefault(s, List.of())) {
                Accumulator acc = accs.computeIfAbsent(a.player(), Accumulator::new);
                switch (a.type()) {
                    case BET -> acc.postflopBets++;
                    case RAISE -> acc.postflopRaises++;
                    case CALL -> acc.postflopCalls++;
                    default -> {
                    }
                }
            }
        }
    }

    private static final class Accumulator {
        final String player;
        int handsPlayed;
        int vpipCount;
        int pfrCount;
        int threeBetCount;
        int threeBetOpportunities;
        int postflopBets;
        int postflopRaises;
        int postflopCalls;

        Accumulator(String player) {
            this.player = player;
        }

        PlayerStats toStats() {
            return new PlayerStats(
                    player, handsPlayed,
                    vpipCount, pfrCount,
                    threeBetCount, threeBetOpportunities,
                    postflopBets, postflopRaises, postflopCalls
            );
        }
    }
}
