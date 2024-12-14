package org.example.indexing;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import java.nio.file.Paths;

public class WikiSearcher {

    private final String indexDirectory;
    private final StandardAnalyzer analyzer;

    public WikiSearcher(String indexDirectory) {
        this.indexDirectory = indexDirectory;
        this.analyzer = new StandardAnalyzer();
    }

    public String searchForBestMatch(String clue) throws Exception {
        QueryBuilder queryBuilder = new QueryBuilder(analyzer);
        Query query = queryBuilder.createBooleanQuery("content", clue);

        try (Directory dir = FSDirectory.open(Paths.get(indexDirectory));
             IndexReader reader = DirectoryReader.open(dir)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, 1);

            if (topDocs.totalHits.value > 0) {
                return searcher.doc(topDocs.scoreDocs[0].doc).get("title");
            }
        }

        return null;
    }
}