package org.example.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLoader {

    public List<String> loadQuestions(String filePath) throws IOException {
        List<String> questions = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            String category = null;
            String question = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (category == null) {
                    category = line;
                } else if (question == null) {
                    question = line;
                } else {
                    questions.add(String.format("%s: %s - Answer: %s", category, question, line));
                    category = question = null;
                }
            }
        }
        return questions;
    }

    public Map<String, String> loadWikipediaData(String filePath) throws IOException {
        Map<String, String> wikiData = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            String title = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[[")) {
                    if (title != null && !content.isEmpty()) {
                        wikiData.put(title, content.toString());
                        content.setLength(0);
                    }
                    title = extractTitle(line);
                } else if (!line.trim().isEmpty()) {
                    content.append(line).append("\n");
                }
            }

            if (title != null && !content.isEmpty()) {
                wikiData.put(title, content.toString());
            }
        }
        return wikiData;
    }

    private String extractTitle(String line) {
        Matcher matcher = Pattern.compile("\\[\\[(.*?)]]").matcher(line);
        return matcher.find() ? matcher.group(1) : null;
    }

    public Map<String, String> loadWikipediaDataFromDirectory(String directoryPath) throws IOException {
        Map<String, String> wikiData = new HashMap<>();
        File dir = new File(directoryPath);

        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("The directory path is invalid.");
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                wikiData.putAll(loadWikipediaData(file.getAbsolutePath()));
            }
        }
        return wikiData;
    }

    public Set<String> loadStopwords(String filePath) throws IOException {
        Set<String> stopwords = new HashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopwords.add(line.trim().toLowerCase());
            }
        }
        return stopwords;
    }
}
