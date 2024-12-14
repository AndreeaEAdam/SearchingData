import org.example.preprocessing.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTest {

    private Tokenizer tokenizer;

    @BeforeEach
    public void setUp() throws Exception {
        InputStream posModelStream = getClass().getClassLoader().getResourceAsStream("en-pos-maxent.bin");

        if (posModelStream == null) {
            throw new IllegalArgumentException("POS model file not found.");
        }

        tokenizer = new Tokenizer(Set.of("the", "a", "of"), posModelStream);
    }

    @Test
    public void testTokenizeTextWithNounsAndVerbs() throws Exception {
        String text = "The dog runs fast.";

        List<String> tokens = tokenizer.tokenize(text);

        assertEquals(List.of("dog", "run", "fast"), tokens);
    }

    @Test
    public void testTokenizeWithStopwords() throws Exception {
        String text = "A cat jumps over the lazy dog.";

        List<String> tokens = tokenizer.tokenize(text);

        assertEquals(List.of("cat", "jump", "over", "lazy", "dog"), tokens);
    }

    @Test
    public void testTokenizeWithPunctuation() throws Exception {
        String text = "Hello, world! Programming is fun.";

        List<String> tokens = tokenizer.tokenize(text);

        assertEquals(List.of("hello", "world", "program", "is", "fun"), tokens);
    }

    @Test
    public void testTokenizeEmptyText() throws Exception {
        String text = "";

        List<String> tokens = tokenizer.tokenize(text);

        assertEquals(List.of(), tokens);
    }

    @Test
    public void testTokenizeWithMixedCase() throws Exception {
        String text = "Dogs LOVE to RUN fast.";

        List<String> tokens = tokenizer.tokenize(text);

        assertEquals(List.of("dog", "love", "to", "run", "fast"), tokens);
    }
}