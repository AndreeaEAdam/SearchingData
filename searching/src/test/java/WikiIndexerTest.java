import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.example.indexing.WikiIndexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WikiIndexerTest {

    private final String indexDirectory = "test_index";
    private WikiIndexer wikiIndexer;

    @BeforeEach
    public void setUp() throws IOException {
        wikiIndexer = new WikiIndexer(indexDirectory);
        clearIndex();
    }

    private void clearIndex() throws IOException {
        try (Directory dir = FSDirectory.open(Paths.get(indexDirectory));
             IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()))) {
            writer.deleteAll();
            writer.commit();
        }
    }

    private void indexData(Map<String, String> wikiData) throws IOException {
        wikiIndexer.indexWikipediaData(wikiData);
    }

    private TopDocs searchIndex(String field, String queryText) throws IOException {
        try (Directory dir = FSDirectory.open(Paths.get(indexDirectory));
             DirectoryReader reader = DirectoryReader.open(dir)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryBuilder queryBuilder = new QueryBuilder(new StandardAnalyzer());
            Query query = queryBuilder.createPhraseQuery(field, queryText);
            return searcher.search(query, 10);
        }
    }

    @Test
    public void testIndexWikipediaData() throws IOException {
        Map<String, String> wikiData = new HashMap<>();
        wikiData.put("Python (programming language)", "Python is an interpreted high-level programming language.");
        wikiData.put("Java (programming language)", "Java is a high-level, class-based, object-oriented programming language.");

        indexData(wikiData);

        verifyIndexContains("title", "Python", "Python (programming language)", "Python is an interpreted high-level programming language.");
        verifyIndexContains("content", "Java", "Java (programming language)", "Java is a high-level, class-based, object-oriented programming language.");
    }

    @Test
    public void testIndexEmptyData() throws IOException {
        Map<String, String> emptyData = new HashMap<>();
        indexData(emptyData);

        try (Directory dir = FSDirectory.open(Paths.get(indexDirectory))) {
            DirectoryReader reader = DirectoryReader.open(dir);
            assertEquals(0, reader.numDocs());
        }
    }

    @Test
    public void testMultipleIndexing() throws IOException {
        Map<String, String> wikiData1 = new HashMap<>();
        wikiData1.put("Python (programming language)", "Python is an interpreted high-level programming language.");
        wikiData1.put("Java (programming language)", "Java is a high-level, class-based, object-oriented programming language.");

        indexData(wikiData1);

        Map<String, String> wikiData2 = new HashMap<>();
        wikiData2.put("C++ (programming language)", "C++ is a high-performance, compiled programming language.");

        indexData(wikiData2);

        try (Directory dir = FSDirectory.open(Paths.get(indexDirectory))) {
            DirectoryReader reader = DirectoryReader.open(dir);
            assertEquals(3, reader.numDocs());
        }
    }

    @Test
    public void testQueryOnIndexedData() throws Exception {
        Map<String, String> wikiData = new HashMap<>();
        wikiData.put("Python (programming language)", "Python is an interpreted high-level programming language.");
        wikiData.put("Java (programming language)", "Java is a high-level, class-based, object-oriented programming language.");

        indexData(wikiData);

        TopDocs results = searchIndex("content", "interpreted");

        assertEquals(1, results.totalHits.value);

        // Retrieve the document by doc ID
        Document doc = getDocumentFromSearchResults(results);
        assertEquals("Python (programming language)", doc.get("title"));
    }

    private Document getDocumentFromSearchResults(TopDocs results) throws IOException {
        ScoreDoc scoreDoc = results.scoreDocs[0];
        try (Directory dir = FSDirectory.open(Paths.get(indexDirectory));
             DirectoryReader reader = DirectoryReader.open(dir)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            return searcher.doc(scoreDoc.doc);
        }
    }

    private void verifyIndexContains(String field, String queryText, String expectedTitle, String expectedContent) throws IOException {
        TopDocs results = searchIndex(field, queryText);
        assertEquals(1, results.totalHits.value);
        Document doc = getDocumentFromSearchResults(results);
        assertEquals(expectedTitle, doc.get("title"));
        assertTrue(doc.get("content").contains(expectedContent));
    }
}
