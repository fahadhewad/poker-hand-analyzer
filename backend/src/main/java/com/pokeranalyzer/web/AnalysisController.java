package com.pokeranalyzer.web;

import com.pokeranalyzer.web.dto.AnalysisResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AnalysisController {

    private final AnalysisService service;

    public AnalysisController(AnalysisService service) {
        this.service = service;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> analyze(@RequestBody String body) {
        if (body == null || body.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Request body must contain hand history text."));
        }
        try {
            AnalysisResponse response = service.analyze(body);
            if (response.handsAnalyzed() == 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No PokerStars hands recognised in input."));
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to parse hand history: " + e.getMessage()));
        }
    }
}
