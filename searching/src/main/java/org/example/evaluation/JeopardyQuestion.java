package org.example.evaluation;

public class JeopardyQuestion {
    String category;
    String clue;
    String answer;

    public JeopardyQuestion(String category, String clue, String answer) {
        this.category = category;
        this.clue = clue;
        this.answer = answer;
    }

    public String getClue() {
        return clue;
    }

    public String getAnswer() {
        return answer;
    }
}
