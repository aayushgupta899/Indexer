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

/**
 * Generates the query terms (700 terms, 7 per line)
 */
public class QueryGenerator {

    private static final String QUERY_FILE_NAME = "QueryTerms700.txt";

    /**
     * @param args compress
     */
    public static void main(String[] args) {
        InvertedIndex invertedIndex = new InvertedIndex();
        String filename = QUERY_FILE_NAME;
        boolean compress = Boolean.parseBoolean(args[0]);
        System.out.println("Starting the query generation process with the following arguments:");
        System.out.println("Is Index Compressed: "+compress);
        System.out.println("Output Query File name: "+QUERY_FILE_NAME);
        System.out.println("*******************************");
        invertedIndex.load(compress, null);
        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.generateQueryTerms(invertedIndex, filename, false);
        System.out.println("Query Terms generated successfully!");
        System.out.println("*******************************");
    }

    /**
     * Generates the query terms and writes them to file
     * @param invertedIndex The inverted index
     * @param filename The output filename
     * @param withStats Whether to write with document frequency and term frequency
     */
    public void generateQueryTerms(InvertedIndex invertedIndex, String filename, boolean withStats)
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
                if(withStats){
                    int termFreq = invertedIndex.getTermFrequency(term);
                    int docFreq = invertedIndex.getDocFrequency(term);
                    sb.append(term+" "+termFreq+" "+docFreq);
                }
                else{
                    sb.append(term+" ");
                }
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
