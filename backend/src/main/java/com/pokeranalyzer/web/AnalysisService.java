package com.pokeranalyzer.web;

import com.pokeranalyzer.analysis.Leak;
import com.pokeranalyzer.analysis.LeakDetectionEngine;
import com.pokeranalyzer.analysis.PlayerClassifier;
import com.pokeranalyzer.analysis.PlayerStats;
import com.pokeranalyzer.analysis.PlayerType;
import com.pokeranalyzer.analysis.StatsCalculator;
import com.pokeranalyzer.model.HandHistory;
import com.pokeranalyzer.parser.HandHistoryParser;
import com.pokeranalyzer.web.dto.AnalysisResponse;
import com.pokeranalyzer.web.dto.PlayerReport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {

    private final HandHistoryParser parser = new HandHistoryParser();
    private final StatsCalculator statsCalculator = new StatsCalculator();
    private final PlayerClassifier classifier = new PlayerClassifier();
    private final LeakDetectionEngine leakEngine = new LeakDetectionEngine();

    public AnalysisResponse analyze(String raw) {
        List<HandHistory> hands = parser.parseAll(raw);
        Map<String, PlayerStats> stats = statsCalculator.calculate(hands);
        Map<String, List<Leak>> leaks = leakEngine.detect(hands);

        List<PlayerReport> reports = new ArrayList<>();
        stats.forEach((player, playerStats) -> {
            PlayerType type = classifier.classify(playerStats);
            List<Leak> playerLeaks = leaks.getOrDefault(player, List.of());
            reports.add(PlayerReport.from(playerStats, type, playerLeaks));
        });
        reports.sort(Comparator.comparing(PlayerReport::name));
        return new AnalysisResponse(hands.size(), reports);
    }
}
