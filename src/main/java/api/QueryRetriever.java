package api;

import index.InvertedIndex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class QueryRetriever {

    public static void main(String[] args) {
        QueryRetriever queryRetriever = new QueryRetriever();
        queryRetriever.retrieveQuery("Query_700.txt", false, 3);
        queryRetriever.retrieveQuery("Query_700.txt", true, 3);
    }

    public void retrieveQuery(String queryFileName, boolean compress, int k) {
        InvertedIndex invertedIndex = new InvertedIndex();
        List<Map.Entry<Integer, Double>> results;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(queryFileName), StandardCharsets.UTF_8));) {
            Instant start = Instant.now();
            String currentLine = reader.readLine();
            while(currentLine != null) {
                String[] queryTerms = currentLine.split("\\s+");
                invertedIndex.load(compress, queryTerms);
                results = invertedIndex.retrieveQuery(queryTerms, k);
                currentLine = reader.readLine();
            }
            Instant end = Instant.now();
            System.out.println("Query with file: "+queryFileName+" and compression: "+compress+" took "+ Duration.between(start, end).toMillis()+" ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
