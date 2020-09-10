package index;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utilities.IndexReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InvertedIndex implements Index {

    private Map<String, PostingList> index;
    private Map<Integer, String> sceneIDMap;
    private Map<Integer, String> playIDMap;
    private Map<Integer, Integer> docLengthMap;

    private static final String LOOKUP_FILE_NAME =  "InvertedIndexLookup.txt";
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

    public InvertedIndex() {

        this.index = new HashMap<>();
        this.sceneIDMap = new HashMap<>();
        this.playIDMap = new HashMap<>();
        this.docLengthMap = new HashMap<>();
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
    public void load(boolean compress) {
        IndexReader indexReader = new IndexReader();
        String invertedFileName = compress ? "InvertedListCompressed" : "InvertedList";
        this.index = indexReader.readIndex(LOOKUP_FILE_NAME, invertedFileName, compress);
        this.sceneIDMap = indexReader.readStringMap(SCENE_ID_MAP_FILE_NAME);
        this.playIDMap = indexReader.readStringMap(PLAY_ID_MAP_FILE_NAME);
        this.docLengthMap = indexReader.readIntegerMap(DOC_LENGTH_MAP_FILE_NAME);
    }

    @Override
    public List<Map.Entry<Integer, Double>> retrieveQuery(String query, int k) {
        throw new NotImplementedException();
    }

    public Map<String, PostingList> getIndex() {
        return index;
    }

    public void setIndex(Map<String, PostingList> index) {
        this.index = index;
    }

}
