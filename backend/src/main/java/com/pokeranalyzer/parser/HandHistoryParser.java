package com.pokeranalyzer.parser;

import com.pokeranalyzer.model.Card;
import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.model.Seat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandHistoryParser {

    private static final Pattern HEADER =
            Pattern.compile("^PokerStars Hand #(\\d+):\\s+(.+?) \\(\\$([\\d.]+)/\\$([\\d.]+).*$");
    private static final Pattern TABLE =
            Pattern.compile("^Table '([^']+)' (\\d+)-max Seat #(\\d+) is the button$");
    private static final Pattern SEAT =
            Pattern.compile("^Seat (\\d+): (\\S+) \\(\\$([\\d.]+) in chips\\)$");
    private static final Pattern DEALT =
            Pattern.compile("^Dealt to (\\S+) \\[(\\w\\w) (\\w\\w)\\]$");
    private static final Pattern BOARD =
            Pattern.compile("^Board \\[(.+)\\]$");

    public List<HandHistory> parseAll(String raw) {
        return Arrays.stream(raw.split("(?m)^(?=PokerStars Hand #)"))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .map(this::parseHand)
                .toList();
    }

    public HandHistory parseHand(String handText) {
        String handId = null;
        String gameType = null;
        double smallBlind = 0;
        double bigBlind = 0;
        String tableName = null;
        int maxSeats = 0;
        int buttonSeat = 0;
        List<Seat> seats = new ArrayList<>();
        String hero = null;
        List<Card> heroCards = List.of();
        List<Card> board = List.of();

        for (String rawLine : handText.split("\\R")) {
            String line = rawLine.strip();
            if (line.isEmpty()) continue;

            Matcher m;
            if ((m = HEADER.matcher(line)).matches()) {
                handId = m.group(1);
                gameType = m.group(2);
                smallBlind = Double.parseDouble(m.group(3));
                bigBlind = Double.parseDouble(m.group(4));
            } else if ((m = TABLE.matcher(line)).matches()) {
                tableName = m.group(1);
                maxSeats = Integer.parseInt(m.group(2));
                buttonSeat = Integer.parseInt(m.group(3));
            } else if ((m = SEAT.matcher(line)).matches()) {
                seats.add(new Seat(
                        Integer.parseInt(m.group(1)),
                        m.group(2),
                        Double.parseDouble(m.group(3))
                ));
            } else if ((m = DEALT.matcher(line)).matches()) {
                hero = m.group(1);
                heroCards = List.of(Card.of(m.group(2)), Card.of(m.group(3)));
            } else if ((m = BOARD.matcher(line)).matches()) {
                String[] tokens = m.group(1).split("\\s+");
                List<Card> cards = new ArrayList<>(tokens.length);
                for (String t : tokens) {
                    cards.add(Card.of(t));
                }
                board = List.copyOf(cards);
            }
        }

        return new HandHistory(
                handId,
                gameType,
                smallBlind,
                bigBlind,
                null,
                tableName,
                maxSeats,
                buttonSeat,
                List.copyOf(seats),
                hero,
                heroCards,
                Map.of(),
                board,
                0,
                0
        );
    }
}
