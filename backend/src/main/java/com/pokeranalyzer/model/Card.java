package com.pokeranalyzer.model;

/** A single playing card, e.g. "Ah" -> rank 'A', suit 'h'. */
public record Card(char rank, char suit) {

    public static Card of(String token) {
        if (token == null || token.length() != 2) {
            throw new IllegalArgumentException("Invalid card token: " + token);
        }
        return new Card(token.charAt(0), token.charAt(1));
    }

    @Override
    public String toString() {
        return "" + rank + suit;
    }
}
