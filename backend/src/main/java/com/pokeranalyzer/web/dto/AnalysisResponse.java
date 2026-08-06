package com.pokeranalyzer.web.dto;

import java.util.List;

public record AnalysisResponse(
        int handsAnalyzed,
        List<PlayerReport> players
) {}
