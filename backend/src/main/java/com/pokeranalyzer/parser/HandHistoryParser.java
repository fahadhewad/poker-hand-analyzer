package com.pokeranalyzer.parser;

import com.pokeranalyzer.model.HandHistory;

import java.util.List;

/**
 * Parses raw PokerStars hand-history text into structured {@link HandHistory} objects.
 *
 * <p>This is the heart of the project — implement {@link #parseAll} and
 * {@link #parseHand} so the tests in {@code HandHistoryParserTest} pass.
 * The fixture {@code src/test/resources/cash-6max.txt} shows the exact input format.
 */
public class HandHistoryParser {

    /** Split a multi-hand dump into individual hands and parse each one. */
    public List<HandHistory> parseAll(String raw) {
        throw new UnsupportedOperationException("TODO: implement parseAll");
    }

    /** Parse a single hand's text block into a {@link HandHistory}. */
    public HandHistory parseHand(String handText) {
        throw new UnsupportedOperationException("TODO: implement parseHand");
    }
}
