package index;

import utilities.Compressor;
import utilities.IndexReader;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

/**
 * Implements the Inverted Index
 */
public class InvertedIndex implements Index {

    /**
     * This stores our actual inverted index
     */
    private Map<String, PostingList> index;
    /**
     * This stores our lookup map, which maps from the term to
     * a list containing offset, bytes written, term frequency and document frequency
     */
    private Map<String, List<String>> lookupMap;
    /**
     * This stores our scene ID Map, which maps from the
     * integer document ID to the scene ID
     */
    private Map<Integer, String> sceneIDMap;
    /**
     * This stores our play ID Map, which maps from the
     * integer document ID to the play ID
     */
    private Map<Integer, String> playIDMap;
    /**
     * This stores our doc length Map, which maps from the
     * integer document ID to the length of the document
     */
    private Map<Integer, Integer> docLengthMap;

    /**
     *  This stores the mapping between the doc ID and the
     *  terms, along with their term counts
     */
    private Map<Integer, Map<String, Integer>> docToTermMap;

    private static final String INVERTED_INDEX_FILE_NAME_COMPRESSED = "InvertedListCompressed";
    private static final String INVERTED_INDEX_FILE_NAME_UNCOMPRESSED = "InvertedList";
    private static final String LOOKUP_FILE_NAME_COMPRESSED =  "InvertedIndexLookupCompressed.txt";
    private static final String LOOKUP_FILE_NAME_UNCOMPRESSED =  "InvertedIndexLookupUncompressed.txt";
    private static final String SCENE_ID_MAP_FILE_NAME = "SceneIDMap.txt";
    private static final String PLAY_ID_MAP_FILE_NAME = "PlayIDMap.txt";
    private static final String DOC_LENGTH_MAP_FILE_NAME = "DocLengthMap.txt";
    private static final String DOC_TO_TERM_MAP_FILE_NAME = "DocTermMap.json";

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

    private String termInvListFile;

    public Map<Integer, Map<String, Integer>> getDocToTermMap() {
        return docToTermMap;
    }

    public void setDocToTermMap(Map<Integer, Map<String, Integer>> docToTermMap) {
        this.docToTermMap = docToTermMap;
    }

    public InvertedIndex() {

        this.index = new HashMap<>();
        this.sceneIDMap = new HashMap<>();
        this.playIDMap = new HashMap<>();
        this.docLengthMap = new HashMap<>();
        this.lookupMap = new HashMap<>();
        this.docToTermMap = new HashMap<>();
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
        long collectionSize = 0;
        for(Map.Entry<Integer, Integer> entry : this.docLengthMap.entrySet()){
            collectionSize += entry.getValue();
        }
        return collectionSize;
    }

    @Override
    public int getDocCount() {
        return this.docLengthMap.size();
    }

    @Override
    public int getDocFrequency(String term) {
//        if(this.index.containsKey(term))
//        {
//            return this.index.get(term).getDocumentCount();
//        }
        if(this.lookupMap.containsKey(term)){
            return Integer.parseInt(this.lookupMap.get(term).get(2));
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

    public Map<String, Integer> getDocumentVector(int docID){
        return this.getDocToTermMap().get(docID);
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
    public PostingList getPostingList(String word) {
        boolean isCompressed = termInvListFile.equals(INVERTED_INDEX_FILE_NAME_COMPRESSED);
        PostingList postings = new PostingList();
        try (RandomAccessFile invertedListReader = new RandomAccessFile(termInvListFile, "rw")){
            List<String> lookupEntry = lookupMap.get(word);
            long offset = Long.parseLong(lookupEntry.get(0));
            int buffLength = Integer.parseInt(lookupEntry.get(1));
            byte[] buffer = new byte[buffLength];
            invertedListReader.seek(offset);
            invertedListReader.read(buffer, 0, buffLength);
            if(isCompressed)
            {
                Compressor compressor = new Compressor();
                IntBuffer intBuffer = IntBuffer.allocate(buffer.length);
                compressor.decompress(buffer, intBuffer);
                int[] data = new int[intBuffer.position()];
                intBuffer.rewind();
                intBuffer.get(data);
                postings.fromIntegerArray(data);
            }
            else
            {
                int off = 0;
                while (off < buffLength) {
                    Posting posting = new Posting();
                    int docID = fromByteArray(Arrays.copyOfRange(buffer, off, off + 4));
                    posting.setDocID(docID);
                    off += 4;
                    int termFrequency = fromByteArray(Arrays.copyOfRange(buffer, off, off + 4));
                    off += 4;
                    Integer[] pos = new Integer[termFrequency];
                    for (int i = 0; i < termFrequency; i++) {
                        pos[i] = fromByteArray(Arrays.copyOfRange(buffer, off, off + 4));
                        off += 4;
                    }
                    posting.setPositions(Arrays.asList(pos));
                    postings.add(posting);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postings;
    }

    private int fromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public int getTermFrequency(String term) {
        int retval = 0;
        if (lookupMap.containsKey(term)) {
            retval = Integer.parseInt(lookupMap.get(term).get(3));
        }
        return retval;
//        if(this.index.containsKey(term)){
//            PostingList postings = this.index.get(term);
//            int termFrequency = 0;
//            for(Posting p : postings.getPostings())
//            {
//                termFrequency += p.getTermFrequency();
//            }
//            return termFrequency;
//        }
//        return 0;
    }

    @Override
    public Set<String> getVocabulary() {
        return this.index.keySet();
    }

    @Override
    public void getQueryPostings(boolean compress, String[] queryTerms){
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
        termInvListFile = compress ? INVERTED_INDEX_FILE_NAME_COMPRESSED : INVERTED_INDEX_FILE_NAME_UNCOMPRESSED;
        this.lookupMap = indexReader.readLookup(lookupFileName);
        this.sceneIDMap = indexReader.readStringMap(SCENE_ID_MAP_FILE_NAME);
        this.playIDMap = indexReader.readStringMap(PLAY_ID_MAP_FILE_NAME);
        this.docLengthMap = indexReader.readIntegerMap(DOC_LENGTH_MAP_FILE_NAME);
        this.docToTermMap = indexReader.readDocTermMap(DOC_TO_TERM_MAP_FILE_NAME);
    }

    @Override
    public List<Map.Entry<Integer, Double>> retrieveQuery(String[] queryTerms, int k, boolean compress) {
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        this.getQueryPostings(compress, queryTerms);
        Map<String, PostingList> postingLists = new HashMap<>();
        for(int i=0; i<queryTerms.length; i++) {
            postingLists.put(queryTerms[i], this.getPostings(queryTerms[i]));
        }
        Map<String, Integer> queryTermCounts = new HashMap<>();
        for(String query : queryTerms){
            queryTermCounts.putIfAbsent(query, 0);
            queryTermCounts.put(query, queryTermCounts.get(query)+1);
        }
        for(int doc=1; doc<=this.getDocCount(); doc++)
        {
            Double score = 0.0;
            for(Map.Entry<String, PostingList> p : postingLists.entrySet())
            {
                int postingIndex = 0;
                Posting posting = null;
                while(postingIndex < p.getValue().getPostings().size()){
                    if(p.getValue().getPostings().get(postingIndex).getDocID() == doc){
                        posting = p.getValue().getPostings().get(postingIndex);
                        break;
                    }
                    postingIndex++;
                }
                if(posting != null && posting.getDocID() == doc) {
                        score += posting.getTermFrequency();
                }
            }
            pq.offer(new AbstractMap.SimpleEntry<>(doc, score));
            if (pq.size() > k) {
                pq.poll();
            }
        }
        List<Map.Entry<Integer, Double>> result = new ArrayList<>();
        while(!pq.isEmpty()){
            result.add(pq.poll());
        }
        result.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        return result;
    }
    public Map<String, PostingList> getIndex() {
        return index;
    }

    public void setIndex(Map<String, PostingList> index) {
        this.index = index;
    }

}
