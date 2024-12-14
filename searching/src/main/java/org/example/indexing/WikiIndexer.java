package org.example.indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class WikiIndexer {

    private final String indexDirectory;
    private final Analyzer analyzer;

    public WikiIndexer(String indexDirectory) {
        this.indexDirectory = indexDirectory;
        this.analyzer = new StandardAnalyzer();
    }

    public void indexWikipediaData(Map<String, String> wikiData) throws IOException {
        try (Directory dir = FSDirectory.open(Paths.get(indexDirectory));
             IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(analyzer))) {

            final int batchSize = 1000;
            int count = 0;

            for (Map.Entry<String, String> entry : wikiData.entrySet()) {
                String title = entry.getKey();
                String content = entry.getValue();

                Document doc = new Document();
                doc.add(new TextField("title", title, Field.Store.YES));
                doc.add(new TextField("content", content, Field.Store.YES));

                writer.addDocument(doc);

                count++;
                if (count % batchSize == 0) {
                    writer.commit();
                }
            }

            if (count % batchSize != 0) {
                writer.commit();
            }
        }
    }
}