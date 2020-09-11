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

/**
 * Retrieves results for queries stored on file from an inverted index
 */
public class QueryRetriever {

    /**
     * @param args filename compress k
     */
    public static void main(String[] args) {
        QueryRetriever queryRetriever = new QueryRetriever();
        String filename = args[0];
        boolean compress = Boolean.parseBoolean(args[1]);
        int k = Integer.parseInt(args[2]);
        System.out.println("Starting the query retrieval process with the following arguments:");
        System.out.println("Input Query File name: "+filename);
        System.out.println("Is Index Compressed: "+compress);
        System.out.println("k: "+k);
        System.out.println("*******************************");
        queryRetriever.retrieveQueryHelper(filename, compress, k);
        queryRetriever.retrieveQueryTimed(filename, compress, k);
        System.out.println("*******************************");
    }

    /**
     * Retrieves the results for query terms and outputs the time taken
     * @param queryFileName The name of the file containing the queries
     * @param compress Whether the index is compressed or not
     * @param k The number of results to return
     */
    public void retrieveQueryTimed(String queryFileName, boolean compress, int k) {
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

    /**
     * Helper method to retrieve the query from disk to cache
     * @param queryFileName The name of the input query file
     * @param compress Whether the index is compressed or not
     * @param k The number of results to return
     */
    private void retrieveQueryHelper(String queryFileName, boolean compress, int k) {
        InvertedIndex invertedIndex = new InvertedIndex();
        List<Map.Entry<Integer, Double>> results;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(queryFileName), StandardCharsets.UTF_8));) {
            String currentLine = reader.readLine();
            while(currentLine != null) {
                String[] queryTerms = currentLine.split("\\s+");
                invertedIndex.load(compress, queryTerms);
                results = invertedIndex.retrieveQuery(queryTerms, k);
                currentLine = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
