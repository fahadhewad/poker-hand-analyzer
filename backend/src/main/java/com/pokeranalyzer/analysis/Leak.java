package com.pokeranalyzer.analysis;

import com.pokeranalyzer.model.Street;

public record Leak(
        String player,
        LeakType type,
        LeakSeverity severity,
        String handId,
        Street street,
        String explanation
) {}
