package com.pokeranalyzer.parser;

import com.pokeranalyzer.model.HandHistory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HandHistoryParserTest {

    private final HandHistoryParser parser = new HandHistoryParser();

    private String sample() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/cash-6max.txt")) {
            assertNotNull(in, "cash-6max.txt fixture missing from test resources");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void splitsAllHands() throws IOException {
        List<HandHistory> hands = parser.parseAll(sample());
        assertEquals(2, hands.size(), "fixture contains exactly two hands");
    }

    @Test
    void parsesHeaderAndStakes() throws IOException {
        HandHistory h = parser.parseAll(sample()).get(0);
        assertEquals("245100100001", h.handId());
        assertEquals("Hold'em No Limit", h.gameType());
        assertEquals(0.25, h.smallBlind(), 0.0001);
        assertEquals(0.50, h.bigBlind(), 0.0001);
    }

    @Test
    void parsesTableButtonAndSeats() throws IOException {
        HandHistory h = parser.parseAll(sample()).get(0);
        assertEquals("Acrux II", h.tableName());
        assertEquals(6, h.maxSeats());
        assertEquals(4, h.buttonSeat());
        assertEquals(6, h.seats().size());
        assertEquals("Hero", h.seats().get(3).player());
        assertEquals(50.00, h.seats().get(3).startingStack(), 0.0001);
    }

    @Test
    void parsesHeroCardsAndBoard() throws IOException {
        HandHistory h = parser.parseAll(sample()).get(0);
        assertEquals("Hero", h.hero());
        assertEquals(2, h.heroCards().size());
        assertEquals('A', h.heroCards().get(0).rank());
        assertEquals('h', h.heroCards().get(0).suit());
        assertEquals(5, h.board().size(), "hand 1 runs to the river");
    }

    @Test
    void secondHandHasNoShowdownButStillParses() throws IOException {
        HandHistory h = parser.parseAll(sample()).get(1);
        assertEquals("245100100002", h.handId());
        assertEquals(5, h.buttonSeat());
        assertEquals(3, h.board().size(), "hand 2 ends on the flop");
    }
}
