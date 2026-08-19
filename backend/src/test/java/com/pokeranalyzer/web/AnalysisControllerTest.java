package com.pokeranalyzer.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    void rejectsEmptyBodyWithStructuredError() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("malformed_request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void rejectsBlankBodyWithStructuredError() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("   \n\t  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_input"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void rejectsInputWithNoHands() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("this is not a poker hand"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_input"));
    }

    @Test
    void multipartFileUploadWorks() throws Exception {
        MockMultipartFile upload = new MockMultipartFile(
                "file", "hands.txt", "text/plain", fixture().getBytes(StandardCharsets.UTF_8)
        );
        mvc.perform(multipart("/api/analyze/file").file(upload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.handsAnalyzed").value(2))
                .andExpect(jsonPath("$.players[0].name").value("Hero"));
    }

    @Test
    void multipartRejectsEmptyFile() throws Exception {
        MockMultipartFile upload = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]
        );
        mvc.perform(multipart("/api/analyze/file").file(upload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_input"));
    }

    @Test
    void healthEndpointReturnsOk() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }
}
