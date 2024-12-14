package org.example.preprocessing;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tokenizer {

    private final Set<String> stopwords;
    private final PorterStemmer stemmer;
    private final POSTaggerME posTagger;

    public Tokenizer(Set<String> stopwords, InputStream posModelStream) throws IOException {
        this.stopwords = stopwords;
        this.stemmer = new PorterStemmer();
        this.posTagger = new POSTaggerME(new POSModel(posModelStream));
    }

    public List<String> tokenize(String text) throws IOException {
        List<String> tokens = new ArrayList<>();
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokenizedWords = tokenizer.tokenize(text);
        String[] posTags = posTagger.tag(tokenizedWords);

        for (int i = 0; i < tokenizedWords.length; i++) {
            String token = tokenizedWords[i].toLowerCase();

            if (stopwords.contains(token) || token.matches("\\p{Punct}")) continue;

            String stemmedToken = token;
            if (posTags[i].startsWith("VB") || posTags[i].startsWith("NN")) {
                stemmedToken = stemmer.stem(token);
            }

            tokens.add(stemmedToken);
        }

        return tokens;
    }

}
