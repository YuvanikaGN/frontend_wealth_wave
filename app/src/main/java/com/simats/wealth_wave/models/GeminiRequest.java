package com.simats.wealth_wave.models;

import java.util.List;

public class GeminiRequest {
    List<Content> contents;

    public GeminiRequest(String userMessage) {
        this.contents = List.of(new Content("user", List.of(new Part(userMessage))));
    }

    static class Content {
        String role;
        List<Part> parts;

        Content(String role, List<Part> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    static class Part {
        String text;

        Part(String text) {
            this.text = text;
        }
    }
}
