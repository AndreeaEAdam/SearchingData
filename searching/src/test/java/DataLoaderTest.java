import org.example.preprocessing.DataLoader;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataLoaderTest {

    private static final String QUESTIONS_FILE_PATH = "src/test/resources/jeopardy_questions.txt";
    private static final String WIKI_FILE_PATH = "src/test/resources/wikipedia_article.txt";
    private static final String STOPWORDS_FILE_PATH = "src/test/resources/stopwords/stopwords.txt";
    private static final String STOPWORDS_EMPTY_FILE_PATH = "src/test/resources/stopwords/stopwords_empty.txt";

    @Test
    public void testLoadQuestions() throws Exception {
        writeToFile(QUESTIONS_FILE_PATH, getJeopardyQuestionsContent());

        DataLoader dataLoader = new DataLoader();
        List<String> questions = dataLoader.loadQuestions(QUESTIONS_FILE_PATH);

        assertNotNull(questions);
        assertEquals(6, questions.size());

        verifyQuestion(questions, "NEWSPAPERS", "The dominant paper in our nation's capital, it's among the top 10 U.S. papers in circulation", "The Washington Post");
        verifyQuestion(questions, "OLD YEAR'S RESOLUTIONS", "The practice of pre-authorizing presidential use of force dates to a 1955 resolution re: this island near mainland China", "Taiwan");
        verifyQuestion(questions, "NEWSPAPERS", "Daniel Hertzberg & James B. Stewart of this paper shared a 1988 Pulitzer for their stories about insider trading", "The Wall Street Journal");
        verifyQuestion(questions, "BROADWAY LYRICS", "Song that says, \"you make me smile with my heart; your looks are laughable, unphotographable\"", "My Funny Valentine");
        verifyQuestion(questions, "POTPOURRI", "In 2011 bell ringers for this charity started accepting digital donations to its red kettle", "The Salvation Army");
        verifyQuestion(questions, "STATE OF THE ART MUSEUM", "The Naples Museum of Art", "Florida");
    }

    private void verifyQuestion(List<String> questions, String category, String question, String answer) {
        assertTrue(questions.stream().anyMatch(q -> q.contains(category) && q.contains(question) && q.contains(answer)));
    }

    @Test
    public void testLoadWikipediaData() throws Exception {
        writeToFile(WIKI_FILE_PATH, getWikipediaContent());

        DataLoader dataLoader = new DataLoader();
        Map<String, String> wikiData = dataLoader.loadWikipediaData(WIKI_FILE_PATH);

        assertNotNull(wikiData);
        assertEquals(3, wikiData.size());

        assertWikiData(wikiData, "BBS", "");
        assertWikiData(wikiData, "Balfour declaration", "");
        assertWikiData(wikiData, "British Standards", "British Standards are the standards produced by BSI Group");
    }

    private void assertWikiData(Map<String, String> wikiData, String key, String expectedValue) {
        assertTrue(wikiData.containsKey(key));
        if (!expectedValue.isEmpty()) {
            assertTrue(wikiData.get(key).contains(expectedValue));
        }
    }

    @Test
    public void testLoadStopwords() throws Exception {
        Set<String> stopwords = new HashSet<>(Arrays.asList("the", "is", "in", "on"));
        writeToFile(STOPWORDS_FILE_PATH, stopwords);

        DataLoader dataLoader = new DataLoader();
        Set<String> loadedStopwords = dataLoader.loadStopwords(STOPWORDS_FILE_PATH);

        assertNotNull(loadedStopwords);
        assertEquals(4, loadedStopwords.size());
        assertStopword(loadedStopwords, "the");
        assertStopword(loadedStopwords, "is");
        assertStopword(loadedStopwords, "in");
        assertStopword(loadedStopwords, "on");
    }

    private void assertStopword(Set<String> stopwords, String stopword) {
        assertTrue(stopwords.contains(stopword));
    }

    @Test
    public void testLoadStopwordsWithEmptyFile() throws Exception {
        writeToFile(STOPWORDS_EMPTY_FILE_PATH, Collections.emptyList());

        DataLoader dataLoader = new DataLoader();
        Set<String> loadedStopwords = dataLoader.loadStopwords(STOPWORDS_EMPTY_FILE_PATH);

        assertNotNull(loadedStopwords);
        assertTrue(loadedStopwords.isEmpty());
    }

    private void writeToFile(String filePath, Object content) throws Exception {
        if (content instanceof String) {
            Files.write(Paths.get(filePath), Arrays.asList(((String) content).split("\n")));
        } else if (content instanceof Set<?> rawSet) {
            Set<String> stopwords = new HashSet<>();
            for (Object obj : rawSet) {
                if (obj instanceof String) {
                    stopwords.add((String) obj);
                }
            }
            Files.write(Paths.get(filePath), new ArrayList<>(stopwords));
        }
    }

    private String getJeopardyQuestionsContent() {
        return """
                NEWSPAPERS
                The dominant paper in our nation's capital, it's among the top 10 U.S. papers in circulation
                The Washington Post
                
                OLD YEAR'S RESOLUTIONS
                The practice of pre-authorizing presidential use of force dates to a 1955 resolution re: this island near mainland China
                Taiwan
                
                NEWSPAPERS
                Daniel Hertzberg & James B. Stewart of this paper shared a 1988 Pulitzer for their stories about insider trading
                The Wall Street Journal
                
                BROADWAY LYRICS
                Song that says, "you make me smile with my heart; your looks are laughable, unphotographable"
                My Funny Valentine
                
                POTPOURRI
                In 2011 bell ringers for this charity started accepting digital donations to its red kettle
                The Salvation Army
                
                STATE OF THE ART MUSEUM (Alex: We'll give you the museum. You give us the state.)
                The Naples Museum of Art
                Florida
                """;
    }

    private String getWikipediaContent() {
        return """
                [[BBS]]
                BBS may refer to:
                ==Technologies==
                ==Organisations==
                ==Science==
                ==Culture==
                ==Other==
                [[Balfour declaration]]
                #REDIRECT Balfour Declaration [tpl]R from other capitalisation[/tpl]
                [[British Standards]]
                CATEGORIES: 1901 establishments in the United Kingdom, British Standards, International Electrotechnical Commission, Standards organizations, Certification marks
                British Standards are the standards produced by BSI Group which is incorporated under a Royal Charter.
                ==How British Standards are made==
                The BSI Group does not produce British Standards. The standards are made through decentralized committees.
                """;
    }
}
