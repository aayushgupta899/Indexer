package api;

import index.InvertedIndex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class QueryGenerator {

    public static void main(String[] args) {
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.load(true);
        String filename = "Query_700.txt";
        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.generateQueryTerms(invertedIndex, filename);
    }
    public void generateQueryTerms(InvertedIndex invertedIndex, String filename)
    {
        Set<String> vocabulary = invertedIndex.getVocabulary();
        List<String> vocabList = new ArrayList<>(vocabulary);
        List<String> lines = new ArrayList<>();
        for(int i=0; i<100; i++)
        {
            StringBuilder sb = new StringBuilder();
            for(int j=0; j<7; j++)
            {
                int randomInt = new Random().nextInt(vocabList.size());
                String term = vocabList.get(randomInt);
                int termFreq = invertedIndex.getTermFrequency(term);
                int docFreq = invertedIndex.getDocFrequency(term);
                sb.append(term+" "+termFreq+" "+docFreq);
                if(j < 6) {
                    sb.append(" ");
                }
            }
            lines.add(sb.toString());
        }
        try{
            Path file = Paths.get(filename);
            Files.write(file, lines, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
