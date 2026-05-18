package com.pokeranalyzer.model;

/**
 * One player action on a given street.
 *
 * <p>{@code amount}   — chips put in for this action (call size, bet size, blind size).
 * <p>{@code toAmount} — for RAISE, the total level raised to
 *                       (PokerStars writes "raises $1.00 to $1.50" → amount 1.00, toAmount 1.50).
 * <p>{@code allIn}    — whether PokerStars marked this action "and is all-in".
 */
public record Action(
        String player,
        Street street,
        ActionType type,
        double amount,
        double toAmount,
        boolean allIn
) {}
