package index;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Index {

    /**
     * Gets the average document length
     * @return Average document length
     */
    double getAverageDocLength();

    /**
     * Gets the size of the collection
     * @return The size of the collection
     */
    long getCollectionSize();

    /**
     * Gets the size of the collection in documents
     * @return The size of the collection in documents
     */
    int getDocCount();

    /**
     * Gets the number of documents containing the term
     * @param term The input term
     * @return The document frequency
     */
    int getDocFrequency(String term);

    /**
     * Gets the length of the document
     * @param docID The document ID to get the length for
     * @return The length of the document
     */
    int getDocLength(int docID);

    /**
     * Gets the name of the document
     * @param docID The external docID
     * @return The name of the document
     */
    String getDocName(int docID);

    /**
     * Gets the posting list for a given term
     * @param term The input term
     * @return The posting list for the input term
     */
    PostingList getPostings(String term);

    /**
     * Gets the frequency of the term
     * @param term The input term for which we need the frequency
     * @return The frequency of the input term
     */
    int getTermFrequency(String term);

    /**
     * Get the unordered set of the vocabulary
     * @return The set of the vocabulary
     */
    Set<String> getVocabulary();

    /**
     * Gets the inverted index from file into memory
     * @param compress Whether the inverted index is compressed or not
     * @param queryTerms The query terms for which the index is to be retrieved.
     *                   If this is null, the entire index is retrieved from disk into memory
     */
    void getInvertedIndex(boolean compress, String[] queryTerms);

    /**
     * Load the index metadata onto memory
     * @param compress Whether the index is compressed or not
     */
    void load(boolean compress);

    /**
     * Returns a query on the index
     * @param queryTerms The array containing query terms
     * @param k The number of results to return
     * @return List of tuples (DocID, Score)
     */
    List<Map.Entry<Integer, Double>> retrieveQuery(String[] queryTerms, int k);


}
