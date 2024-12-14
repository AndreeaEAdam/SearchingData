package org.example;

import org.example.evaluation.AnswerEvaluator;
import org.example.evaluation.JeopardyQuestion;
import org.example.indexing.WikiIndexer;
import org.example.indexing.WikiSearcher;
import org.example.preprocessing.DataLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainEvaluation {

    public static void main(String[] args) throws Exception {
        DataLoader dataLoader = new DataLoader();

        List<String> jeopardyQuestions = dataLoader.loadQuestions("C:\\Users\\ASUS\\IdeaProjects\\SearchingData2\\searching\\src\\main\\resources\\data\\raw\\jeopardy_questions.txt");
        Map<String, String> wikiData = dataLoader.loadWikipediaDataFromDirectory("C:\\Users\\ASUS\\IdeaProjects\\SearchingData2\\searching\\src\\main\\resources\\data\\raw\\wikipedia_articles");
        Set<String> stopwords = dataLoader.loadStopwords("C:\\Users\\ASUS\\IdeaProjects\\SearchingData2\\searching\\src\\main\\resources\\data\\stopwords\\stopwords.txt");

        WikiIndexer wikiIndexer = new WikiIndexer("C:\\Users\\ASUS\\IdeaProjects\\SearchingData2\\searching\\src\\main\\resources\\index");
        wikiIndexer.indexWikipediaData(wikiData);

        WikiSearcher wikiSearcher = new WikiSearcher("C:\\Users\\ASUS\\IdeaProjects\\SearchingData2\\searching\\src\\main\\resources\\index");
        AnswerEvaluator answerEvaluator = new AnswerEvaluator(wikiSearcher, stopwords);

        for (String questionData : jeopardyQuestions) {
            String[] parts = questionData.split(" - Answer: ");
            if (parts.length == 2) {
                String clue = parts[0].split(": ")[1];
                String correctAnswer = parts[1];
                JeopardyQuestion question = new JeopardyQuestion(parts[0].split(": ")[0], clue, correctAnswer);
                answerEvaluator.evaluateAnswers(List.of(question));
            }
        }
    }
}
