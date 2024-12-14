package org.example.preprocessing;

public class TextCleaner {

    public String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        text = text.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", " ").trim();
        return text;
    }
}