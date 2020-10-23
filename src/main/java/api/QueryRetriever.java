package api;

import index.InvertedIndex;
import retrieval.BM25Model;
import retrieval.QueryLikelihoodDirModel;
import retrieval.QueryLikelihoodJMModel;
import retrieval.VectorSpaceModel;

import java.io.*;
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
        System.out.println("Loading Index metadata.....");
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.load(compress);
        System.out.println("Index metadata loaded!");
        System.out.println("*******************************");
        queryRetriever.retrieveQueryVectorSpace(invertedIndex, filename, compress, k);
        queryRetriever.retrieveQueryBM25(invertedIndex, filename, compress, k);
        queryRetriever.retrieveQueryQLJM(invertedIndex, filename, compress, k);
        queryRetriever.retrieveQueryQLDir(invertedIndex, filename, compress, k);
        System.out.println("*******************************");
    }

    /**
     * Retrieves the results for query terms and outputs the time taken
     * @param invertedIndex The inverted index
     * @param queryFileName The name of the file containing the queries
     * @param compress Whether the index is compressed or not
     * @param k The number of results to return
     */
    public void retrieveQueryTimed(InvertedIndex invertedIndex, String queryFileName, boolean compress, int k) {
        List<Map.Entry<Integer, Double>> results;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(queryFileName), StandardCharsets.UTF_8));) {
            Instant start = Instant.now();
            String currentLine = reader.readLine();
            while(currentLine != null) {
                String[] queryTerms = currentLine.split("\\s+");
                results = invertedIndex.retrieveQuery(queryTerms, k, compress);
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
    private void retrieveQueryHelper(InvertedIndex invertedIndex, String queryFileName, boolean compress, int k) {
        List<Map.Entry<Integer, Double>> results;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(queryFileName), StandardCharsets.UTF_8));) {
            String currentLine = reader.readLine();
            while(currentLine != null) {
                String[] queryTerms = currentLine.split("\\s+");
                results = invertedIndex.retrieveQuery(queryTerms, k, compress);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the results for query terms using Vector Space Model
     * @param invertedIndex The inverted index
     * @param queryFileName The name of the file containing the queries
     * @param compress Whether the index is compressed or not
     * @param k The number of results to return
     */
    public void retrieveQueryVectorSpace(InvertedIndex invertedIndex, String queryFileName, boolean compress, int k) {
        List<Map.Entry<Integer, Double>> results;
        VectorSpaceModel vectorSpaceModel = new VectorSpaceModel();
        String runID = "guaayush-vs-logtf-logidf";
        String outputFile = "vs.trecrun";
        int queryNum = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(queryFileName), StandardCharsets.UTF_8));
             PrintWriter outputWriter = new PrintWriter(outputFile);) {
            Instant start = Instant.now();
            String currentLine = reader.readLine();
            while(currentLine != null) {
                String[] queryTerms = currentLine.split("\\s+");
                results = vectorSpaceModel.retrieveQuery(invertedIndex, queryTerms, k, compress);
                int rank = 1;
                for(Map.Entry<Integer, Double> entry : results){
                    outputWriter.println("Q"+queryNum+"\tskip\t"+invertedIndex.getDocName(entry.getKey())+
                                            "\t"+ rank + "\t" + entry.getValue()+ "\t" + runID);
                    rank++;
                }
                queryNum++;
                currentLine = reader.readLine();
            }
            Instant end = Instant.now();
            System.out.println("Query with file: "+queryFileName+", model Vector Space and compression: "+compress+" took "+ Duration.between(start, end).toMillis()+" ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the results for query terms using BM25 Model
     * @param invertedIndex The inverted index
     * @param queryFileName The name of the file containing the queries
     * @param compress Whether the index is compressed or not
     * @param k The number of results to return
     */
    public void retrieveQueryBM25(InvertedIndex invertedIndex, String queryFileName, boolean compress, int k) {
        List<Map.Entry<Integer, Double>> results;
        BM25Model bm25Model = new BM25Model(1.5, 500, 0.75, 0,0);
        String runID = "guaayush-bm25-1.5-500-0.75";
        String outputFile = "bm25.trecrun";
        int queryNum = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(queryFileName), StandardCharsets.UTF_8));
             PrintWriter outputWriter = new PrintWriter(outputFile)) {
            Instant start = Instant.now();
            String currentLine = reader.readLine();
            while(currentLine != null) {
                String[] queryTerms = currentLine.split("\\s+");
                results = bm25Model.retrieveQuery(invertedIndex, queryTerms, k, compress);
                int rank = 1;
                for(Map.Entry<Integer, Double> entry : results){
                    outputWriter.println("Q"+queryNum+"\tskip\t"+invertedIndex.getDocName(entry.getKey())+
                            "\t"+ rank + "\t" + entry.getValue()+ "\t" + runID);
                    rank++;
                }
                queryNum++;
                currentLine = reader.readLine();
            }
            Instant end = Instant.now();
            System.out.println("Query with file: "+queryFileName+", model BM25 and compression: "+compress+" took "+ Duration.between(start, end).toMillis()+" ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the results for query terms using Query Likelihood Model with Jelinik-Mercer smoothing
     * @param invertedIndex The inverted index
     * @param queryFileName The name of the file containing the queries
     * @param compress Whether the index is compressed or not
     * @param k The number of results to return
     */
    public void retrieveQueryQLJM(InvertedIndex invertedIndex, String queryFileName, boolean compress, int k) {
        List<Map.Entry<Integer, Double>> results;
        QueryLikelihoodJMModel queryLikelihoodJMModel = new QueryLikelihoodJMModel(0.2);
        String runID = "guaayush-ql-jm-0.2";
        String outputFile = "ql-jm.trecrun";
        int queryNum = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(queryFileName), StandardCharsets.UTF_8));
             PrintWriter outputWriter = new PrintWriter(outputFile)) {
            Instant start = Instant.now();
            String currentLine = reader.readLine();
            while(currentLine != null) {
                String[] queryTerms = currentLine.split("\\s+");
                results = queryLikelihoodJMModel.retrieveQuery(invertedIndex, queryTerms, k, compress);
                int rank = 1;
                for(Map.Entry<Integer, Double> entry : results){
                    outputWriter.println("Q"+queryNum+"\tskip\t"+invertedIndex.getDocName(entry.getKey())+
                            "\t"+ rank + "\t" + entry.getValue()+ "\t" + runID);
                    rank++;
                }
                queryNum++;
                currentLine = reader.readLine();
            }
            Instant end = Instant.now();
            System.out.println("Query with file: "+queryFileName+", model Query Likelihood with Jelinik-Mercer smoothing and compression: "+compress+" took "+ Duration.between(start, end).toMillis()+" ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the results for query terms using Query Likelihood Model with Dirchlet smoothing
     * @param invertedIndex The inverted index
     * @param queryFileName The name of the file containing the queries
     * @param compress Whether the index is compressed or not
     * @param k The number of results to return
     */
    public void retrieveQueryQLDir(InvertedIndex invertedIndex, String queryFileName, boolean compress, int k) {
        List<Map.Entry<Integer, Double>> results;
        long collectionSize = invertedIndex.getCollectionSize();
        QueryLikelihoodDirModel queryLikelihoodDirModel = new QueryLikelihoodDirModel(1200, collectionSize);
        String runID = "guaayush-ql-dir-1200";
        String outputFile = "ql-dir.trecrun";
        int queryNum = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(queryFileName), StandardCharsets.UTF_8));
             PrintWriter outputWriter = new PrintWriter(outputFile)) {
            Instant start = Instant.now();
            String currentLine = reader.readLine();
            while(currentLine != null) {
                String[] queryTerms = currentLine.split("\\s+");
                results = queryLikelihoodDirModel.retrieveQuery(invertedIndex, queryTerms, k, compress);
                int rank = 1;
                for(Map.Entry<Integer, Double> entry : results){
                    outputWriter.println("Q"+queryNum+"\tskip\t"+invertedIndex.getDocName(entry.getKey())+
                            "\t"+ rank + "\t" + entry.getValue()+ "\t" + runID);
                    rank++;
                }
                queryNum++;
                currentLine = reader.readLine();
            }
            Instant end = Instant.now();
            System.out.println("Query with file: "+queryFileName+", model Query Likelihood with Dirchlet smoothing and compression: "+compress+" took "+ Duration.between(start, end).toMillis()+" ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
