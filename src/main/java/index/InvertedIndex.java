package index;

import utilities.IndexReader;

import java.util.*;

/**
 * Implements the Inverted Index
 */
public class InvertedIndex implements Index {

    private Map<String, PostingList> index;
    private Map<String, List<String>> lookupMap;
    private Map<Integer, String> sceneIDMap;
    private Map<Integer, String> playIDMap;
    private Map<Integer, Integer> docLengthMap;

    private static final String INVERTED_INDEX_FILE_NAME_COMPRESSED = "InvertedListCompressed";
    private static final String INVERTED_INDEX_FILE_NAME_UNCOMPRESSED = "InvertedList";
    private static final String LOOKUP_FILE_NAME_COMPRESSED =  "InvertedIndexLookupCompressed.txt";
    private static final String LOOKUP_FILE_NAME_UNCOMPRESSED =  "InvertedIndexLookupUncompressed.txt";
    private static final String SCENE_ID_MAP_FILE_NAME = "SceneIDMap.txt";
    private static final String PLAY_ID_MAP_FILE_NAME = "PlayIDMap.txt";
    private static final String DOC_LENGTH_MAP_FILE_NAME = "DocLengthMap.txt";

    public Map<Integer, String> getSceneIDMap() {
        return sceneIDMap;
    }

    public Map<Integer, String> getPlayIDMap() {
        return playIDMap;
    }

    public Map<Integer, Integer> getDocLengthMap() {
        return docLengthMap;
    }

    public Map<String, List<String>> getLookupMap() {
        return lookupMap;
    }

    public InvertedIndex() {

        this.index = new HashMap<>();
        this.sceneIDMap = new HashMap<>();
        this.playIDMap = new HashMap<>();
        this.docLengthMap = new HashMap<>();
        this.lookupMap = new HashMap<>();
    }

    @Override
    public double getAverageDocLength() {
        double sum = 0;
        for(Map.Entry<Integer, Integer> entry : this.docLengthMap.entrySet())
        {
            sum += entry.getValue();
        }
        return sum / this.docLengthMap.size();
    }

    @Override
    public long getCollectionSize() {
        int collectionSize = 0;
        for(String term : this.index.keySet())
        {
            collectionSize += this.getTermFrequency(term);
        }
        return collectionSize;
    }

    @Override
    public int getDocCount() {
        return this.docLengthMap.size();
    }

    @Override
    public int getDocFrequency(String term) {
        if(this.index.containsKey(term))
        {
            return this.index.get(term).getDocumentCount();
        }
        return -1;
    }

    @Override
    public int getDocLength(int docID) {
        return this.docLengthMap.containsKey(docID) ? docLengthMap.get(docID) : -1;
    }

    @Override
    public String getDocName(int docID) {

        return this.sceneIDMap.containsKey(docID) ? sceneIDMap.get(docID) : null;
    }

    @Override
    public PostingList getPostings(String term) {
        if(this.index.containsKey(term)) {
            return this.index.get(term);
        }
        else {
            return null;
        }
    }

    @Override
    public int getTermFrequency(String term) {
        if(this.index.containsKey(term)){
            PostingList postings = this.index.get(term);
            int termFrequency = 0;
            for(Posting p : postings.getPostings())
            {
                termFrequency += p.getTermFrequency();
            }
            return termFrequency;
        }
        return -1;
    }

    @Override
    public Set<String> getVocabulary() {
        return this.index.keySet();
    }

    @Override
    public void getInvertedIndex(boolean compress, String[] queryTerms){
        Map<String, List<String>> lookupMap;
        if(queryTerms != null) {
            lookupMap = new HashMap<>();
            for (String term : queryTerms) {
                lookupMap.put(term, this.lookupMap.get(term));
            }
        }
        else {
            lookupMap = this.lookupMap;
        }
        IndexReader indexReader = new IndexReader();
        String invertedFileName = compress ? INVERTED_INDEX_FILE_NAME_COMPRESSED : INVERTED_INDEX_FILE_NAME_UNCOMPRESSED;
        this.index = indexReader.readIndex(invertedFileName, lookupMap, compress);
    }

    @Override
    public void load(boolean compress) {
        IndexReader indexReader = new IndexReader();
        String lookupFileName = compress ? LOOKUP_FILE_NAME_COMPRESSED : LOOKUP_FILE_NAME_UNCOMPRESSED;
        this.lookupMap = indexReader.readLookup(lookupFileName);
        this.sceneIDMap = indexReader.readStringMap(SCENE_ID_MAP_FILE_NAME);
        this.playIDMap = indexReader.readStringMap(PLAY_ID_MAP_FILE_NAME);
        this.docLengthMap = indexReader.readIntegerMap(DOC_LENGTH_MAP_FILE_NAME);
    }


    @Override
    public List<Map.Entry<Integer, Double>> retrieveQuery(String[] queryTerms, int k) {
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(new CustomComparator());
        PostingList[] postingLists = new PostingList[queryTerms.length];
        for(int i=0; i<postingLists.length; i++) {
            postingLists[i] = this.getPostings(queryTerms[i]);
        }
        for(int doc=1; doc<=this.getDocCount(); doc++)
        {
            Double score = 0.0;
            for(PostingList p : postingLists)
            {
                int postingIndex = 0;
                Posting posting = null;
                while(postingIndex < p.getPostings().size()){
                    if(p.getPostings().get(postingIndex).getDocID() == doc){
                        posting = p.getPostings().get(postingIndex);
                        break;
                    }
                    postingIndex++;
                }
                if(posting != null && posting.getDocID() == doc) {
                    score += posting.getTermFrequency();
                }
            }
            pq.offer(new AbstractMap.SimpleEntry<Integer, Double>(doc, score));
        }
        List<Map.Entry<Integer, Double>> result = new ArrayList<>();
        while(!pq.isEmpty() && k > 0)
        {
            result.add(pq.poll());
            k--;
        }
        return result;
    }

    public Map<String, PostingList> getIndex() {
        return index;
    }

    public void setIndex(Map<String, PostingList> index) {
        this.index = index;
    }

}

/**
 * Custom Comparator which sorts a priority queue in descending order
 */
class CustomComparator implements Comparator<Map.Entry<Integer, Double>>{

    @Override
    public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
        return Double.compare(o2.getValue(), o1.getValue());
    }
}
