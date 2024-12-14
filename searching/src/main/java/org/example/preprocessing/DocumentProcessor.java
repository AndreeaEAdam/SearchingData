package org.example.preprocessing;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentProcessor {

    private final TextCleaner textCleaner;
    private final Tokenizer tokenizer;

    public DocumentProcessor(Set<String> stopwords, InputStream posModelStream) throws Exception {
        this.textCleaner = new TextCleaner();
        this.tokenizer = new Tokenizer(stopwords, posModelStream);
    }

    public Map<String, String> processDocuments(Map<String, String> documents) throws Exception {
        Map<String, String> processedDocs = new HashMap<>();
        for (Map.Entry<String, String> entry : documents.entrySet()) {
            String content = textCleaner.cleanText(entry.getValue());
            List<String> tokens = tokenizer.tokenize(content);
            processedDocs.put(entry.getKey(), String.join(" ", tokens));
        }
        return processedDocs;
    }
}
