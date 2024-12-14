package org.example.evaluation;

import org.example.indexing.WikiSearcher;

import java.util.List;
import java.util.Set;

public class AnswerEvaluator {
    private final WikiSearcher wikiSearcher;
    private final Set<String> stopwords;
    private int correctAnswers = 0;
    private int totalQuestions = 0;

    // Constructor
    public AnswerEvaluator(WikiSearcher wikiSearcher, Set<String> stopwords) {
        this.wikiSearcher = wikiSearcher;
        this.stopwords = stopwords;
    }

    // Method to remove stopwords from a given text
    private String removeStopwords(String text) {
        String[] words = text.split("\\s+");
        StringBuilder cleanedText = new StringBuilder();

        for (String word : words) {
            if (!stopwords.contains(word.toLowerCase())) {
                cleanedText.append(word).append(" ");
            }
        }
        return cleanedText.toString().trim();
    }

    // Method to evaluate the answers for each JeopardyQuestion
    public void evaluateAnswers(List<JeopardyQuestion> questions) {
        for (JeopardyQuestion question : questions) {
            String cleanedClue = removeStopwords(question.getClue());  // Clean the clue by removing stopwords
            String correctAnswer = question.getAnswer();
            String cleanedAnswer = removeStopwords(correctAnswer);  // Clean the correct answer by removing stopwords

            String retrievedAnswer = null;
            try {
                retrievedAnswer = wikiSearcher.searchForBestMatch(cleanedClue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            String cleanedRetrievedAnswer = removeStopwords(retrievedAnswer);

            if (evaluateAnswer(cleanedClue, cleanedAnswer, cleanedRetrievedAnswer)) {
                correctAnswers++;
            }
            totalQuestions++;
        }

        // After evaluating all answers, calculate and print the percentage of correct answers
        printResults();
    }

    private boolean evaluateAnswer(String clue, String correctAnswer, String retrievedAnswer) {
        System.out.println("Clue: " + clue);
        System.out.println("Correct Answer: " + correctAnswer);
        System.out.println("Retrieved Answer: " + retrievedAnswer);
        boolean isCorrect = correctAnswer.equalsIgnoreCase(retrievedAnswer);
        if (isCorrect) {
            System.out.println("The answer is correct!\n\n");
        } else {
            System.out.println("The answer is incorrect.\n\n");
        }
        return isCorrect;
    }

    private void printResults() {
        if (totalQuestions > 0) {
            double percentage = ((double) correctAnswers / totalQuestions) * 100;
            System.out.printf("Percentage of Correct Answers: %.2f%%\n", percentage);
        } else {
            System.out.println("No questions evaluated.");
        }
    }
}
