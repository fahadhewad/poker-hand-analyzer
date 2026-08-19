package com.pokeranalyzer.web;

import com.pokeranalyzer.web.dto.AnalysisResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AnalysisController {

    private final AnalysisService service;

    public AnalysisController(AnalysisService service) {
        this.service = service;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.TEXT_PLAIN_VALUE)
    public AnalysisResponse analyze(@RequestBody String body) {
        return runAnalysis(body);
    }

    @PostMapping(value = "/analyze/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalysisResponse analyzeFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidHandHistoryException("No file uploaded, or the file is empty.");
        }
        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InvalidHandHistoryException("Could not read uploaded file.", e);
        }
        return runAnalysis(content);
    }

    private AnalysisResponse runAnalysis(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidHandHistoryException("Hand history text is empty.");
        }
        AnalysisResponse response = service.analyze(raw);
        if (response.handsAnalyzed() == 0) {
            throw new InvalidHandHistoryException("No PokerStars hands recognised in input.");
        }
        return response;
    }
}
