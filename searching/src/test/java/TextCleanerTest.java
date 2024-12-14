import org.example.preprocessing.TextCleaner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TextCleanerTest {

    @Test
    public void testCleanText() {
        TextCleaner textCleaner = new TextCleaner();
        String inputText = " Hello, world!   How are you?  ";
        String expectedOutput = "hello world how are you";
        assertEquals(expectedOutput, textCleaner.cleanText(inputText));
    }

    @Test
    public void testEmptyText() {
        TextCleaner textCleaner = new TextCleaner();
        assertEquals("", textCleaner.cleanText(""));
    }

    @Test
    public void testNullText() {
        TextCleaner textCleaner = new TextCleaner();
        assertNull(textCleaner.cleanText(null));
    }
}
