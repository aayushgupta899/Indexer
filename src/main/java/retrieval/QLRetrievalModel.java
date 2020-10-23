package retrieval;

import index.InvertedIndex;

import java.util.List;
import java.util.Map;

public interface QLRetrievalModel {
    /**
     * Returns a query on the index
     * @param index The inverted index
     * @param queryTerms The array containing query terms
     * @param k The number of results to return
     * @param compress Whether the index is compressed
     * @return List of tuples (DocID, Score)
     */
    List<Map.Entry<Integer, Double>> retrieveQuery(InvertedIndex index, String[] queryTerms, int k, boolean compress);

    /**
     * The scoring function
     * @param termFrequency The term frequency
     * @param collectionTermFrequency The collection term frequency
     * @param documentLength The document length
     * @return The score
     */
    double scoreOccurrence(int termFrequency, int collectionTermFrequency, int documentLength);
}
