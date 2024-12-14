import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.example.indexing.WikiIndexer;
import org.example.indexing.WikiSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class WikiSearcherTest {

    private WikiIndexer wikiIndexer;
    private WikiSearcher wikiSearcher;

    @BeforeEach
    public void setUp() throws IOException {
        String indexDirectory = "test_index";
        wikiIndexer = new WikiIndexer(indexDirectory);
        wikiSearcher = new WikiSearcher(indexDirectory);

        try (FSDirectory dir = FSDirectory.open(Paths.get(indexDirectory));
             IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()))) {
            writer.deleteAll();
            writer.commit();
        }
    }

    @Test
    public void testSearchForBestMatch() throws Exception {
        Map<String, String> wikiData = new HashMap<>();
        wikiData.put("Python (programming language)", "Python is an interpreted high-level programming language.");
        wikiData.put("Java (programming language)", "Java is a high-level, class-based, object-oriented programming language.");
        wikiData.put("C++ (programming language)", "C++ is a high-performance, compiled programming language.");

        wikiIndexer.indexWikipediaData(wikiData);

        String clue = "interpreted high-level programming";
        String result = wikiSearcher.searchForBestMatch(clue);

        assertEquals("Python (programming language)", result);
    }

    @Test
    public void testSearchForNoMatch() throws Exception {
        Map<String, String> wikiData = new HashMap<>();
        wikiData.put("Python (programming language)", "Python is an interpreted high-level programming language.");
        wikiData.put("Java (programming language)", "Java is a high-level, class-based, object-oriented programming language.");
        wikiData.put("C++ (programming language)", "C++ is a high-performance, compiled programming language.");

        wikiIndexer.indexWikipediaData(wikiData);

        String clue = "machine learning";
        String result = wikiSearcher.searchForBestMatch(clue);

        assertNull(result);
    }
}