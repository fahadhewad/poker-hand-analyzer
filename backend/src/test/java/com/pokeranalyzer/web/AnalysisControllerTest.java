package com.pokeranalyzer.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnalysisControllerTest {

    @Autowired
    MockMvc mvc;

    private String fixture() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/cash-6max.txt")) {
            assertNotNull(in, "cash-6max.txt fixture missing");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void analyzesFixtureHands() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(fixture()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.handsAnalyzed").value(2))
                .andExpect(jsonPath("$.players").isArray())
                .andExpect(jsonPath("$.players[0].name").value("Hero"))
                .andExpect(jsonPath("$.players[0].handsPlayed").value(2))
                .andExpect(jsonPath("$.players[0].vpip").value(0.5));
    }

    @Test
    void rejectsEmptyBody() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsInputWithNoHands() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("this is not a poker hand"))
                .andExpect(status().isBadRequest());
    }
}
