package com.pokeranalyzer.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * One fully-parsed hand. {@code actions} maps each street to the ordered list of
 * actions taken on it.
 */
public record HandHistory(
        String handId,
        String gameType,
        double smallBlind,
        double bigBlind,
        LocalDateTime dealtAt,
        String tableName,
        int maxSeats,
        int buttonSeat,
        List<Seat> seats,
        String hero,
        List<Card> heroCards,
        Map<Street, List<Action>> actions,
        List<Card> board,
        double totalPot,
        double rake
) {}
