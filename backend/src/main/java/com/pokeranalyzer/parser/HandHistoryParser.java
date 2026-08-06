package com.pokeranalyzer.parser;

import com.pokeranalyzer.model.Action;
import com.pokeranalyzer.model.ActionType;
import com.pokeranalyzer.model.Card;
import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.model.Seat;
import com.pokeranalyzer.model.Street;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
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
    private static final Pattern TOTAL_POT =
            Pattern.compile("^Total pot \\$([\\d.]+) \\| Rake \\$([\\d.]+)$");

    private static final Pattern POST_SB =
            Pattern.compile("^(\\S+): posts small blind \\$([\\d.]+)$");
    private static final Pattern POST_BB =
            Pattern.compile("^(\\S+): posts big blind \\$([\\d.]+)$");
    private static final Pattern POST_ANTE =
            Pattern.compile("^(\\S+): posts the ante \\$([\\d.]+)$");
    private static final Pattern FOLD =
            Pattern.compile("^(\\S+): folds.*$");
    private static final Pattern CHECK =
            Pattern.compile("^(\\S+): checks$");
    private static final Pattern CALL =
            Pattern.compile("^(\\S+): calls \\$([\\d.]+)( and is all-in)?$");
    private static final Pattern BET =
            Pattern.compile("^(\\S+): bets \\$([\\d.]+)( and is all-in)?$");
    private static final Pattern RAISE =
            Pattern.compile("^(\\S+): raises \\$([\\d.]+) to \\$([\\d.]+)( and is all-in)?$");

    public List<HandHistory> parseAll(String raw) {
        return Arrays.stream(raw.split("(?m)^(?=PokerStars Hand #)"))
                .map(String::strip)
                .filter(s -> s.startsWith("PokerStars Hand #"))
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
        double totalPot = 0;
        double rake = 0;

        Map<Street, List<Action>> actions = new EnumMap<>(Street.class);
        for (Street s : Street.values()) {
            actions.put(s, new ArrayList<>());
        }

        Street street = Street.PREFLOP;
        boolean inSummary = false;

        for (String rawLine : handText.split("\\R")) {
            String line = rawLine.strip();
            if (line.isEmpty()) continue;

            if (line.equals("*** HOLE CARDS ***")) {
                street = Street.PREFLOP;
                continue;
            }
            if (line.startsWith("*** FLOP ***")) {
                street = Street.FLOP;
                continue;
            }
            if (line.startsWith("*** TURN ***")) {
                street = Street.TURN;
                continue;
            }
            if (line.startsWith("*** RIVER ***")) {
                street = Street.RIVER;
                continue;
            }
            if (line.equals("*** SHOW DOWN ***")) {
                street = Street.SHOWDOWN;
                continue;
            }
            if (line.equals("*** SUMMARY ***")) {
                inSummary = true;
                continue;
            }

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
            } else if (!inSummary && (m = SEAT.matcher(line)).matches()) {
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
            } else if ((m = TOTAL_POT.matcher(line)).matches()) {
                totalPot = Double.parseDouble(m.group(1));
                rake = Double.parseDouble(m.group(2));
            } else if (!inSummary) {
                Action action = parseAction(line, street);
                if (action != null) {
                    actions.get(action.street()).add(action);
                }
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
                Map.copyOf(actions),
                board,
                totalPot,
                rake
        );
    }

    private Action parseAction(String line, Street street) {
        Matcher m;
        if ((m = POST_SB.matcher(line)).matches()) {
            return new Action(m.group(1), Street.PREFLOP, ActionType.POST_SMALL_BLIND,
                    Double.parseDouble(m.group(2)), 0, false);
        }
        if ((m = POST_BB.matcher(line)).matches()) {
            return new Action(m.group(1), Street.PREFLOP, ActionType.POST_BIG_BLIND,
                    Double.parseDouble(m.group(2)), 0, false);
        }
        if ((m = POST_ANTE.matcher(line)).matches()) {
            return new Action(m.group(1), Street.PREFLOP, ActionType.POST_ANTE,
                    Double.parseDouble(m.group(2)), 0, false);
        }
        if ((m = FOLD.matcher(line)).matches()) {
            return new Action(m.group(1), street, ActionType.FOLD, 0, 0, false);
        }
        if ((m = CHECK.matcher(line)).matches()) {
            return new Action(m.group(1), street, ActionType.CHECK, 0, 0, false);
        }
        if ((m = CALL.matcher(line)).matches()) {
            return new Action(m.group(1), street, ActionType.CALL,
                    Double.parseDouble(m.group(2)), 0, m.group(3) != null);
        }
        if ((m = BET.matcher(line)).matches()) {
            return new Action(m.group(1), street, ActionType.BET,
                    Double.parseDouble(m.group(2)), 0, m.group(3) != null);
        }
        if ((m = RAISE.matcher(line)).matches()) {
            return new Action(m.group(1), street, ActionType.RAISE,
                    Double.parseDouble(m.group(2)),
                    Double.parseDouble(m.group(3)),
                    m.group(4) != null);
        }
        return null;
    }
}
