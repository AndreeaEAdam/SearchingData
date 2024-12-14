import org.example.preprocessing.DocumentProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentProcessorTest {

    private DocumentProcessor documentProcessor;

    @BeforeEach
    public void setUp() throws Exception {
        InputStream posModelStream = getClass().getClassLoader().getResourceAsStream("en-pos-maxent.bin");

        if (posModelStream == null) {
            throw new IllegalArgumentException("POS model file not found.");
        }

        Set<String> stopwords = Set.of("the", "a", "of");
        documentProcessor = new DocumentProcessor(stopwords, posModelStream);
    }

    @Test
    public void testProcessDocuments() throws Exception {
        Map<String, String> documents = Map.of(
                "Doc1", "The quick brown fox jumps over a lazy dog.",
                "Doc2", "A journey of a thousand miles begins with a single step."
        );

        Map<String, String> expectedProcessedDocs = Map.of(
                "Doc1", "quick brown fox jump over lazy dog",
                "Doc2", "journei thousand mile begin with single step"
        );

        Map<String, String> processedDocs = documentProcessor.processDocuments(documents);

        assertEquals(expectedProcessedDocs, processedDocs);
    }
}