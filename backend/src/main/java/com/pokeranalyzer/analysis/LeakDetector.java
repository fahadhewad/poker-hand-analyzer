package com.pokeranalyzer.analysis;

import com.pokeranalyzer.model.HandHistory;

import java.util.List;

public interface LeakDetector {
    List<Leak> detect(HandHistory hand);
}
