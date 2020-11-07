package api;

import index.InvertedIndex;
import index.PostingList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utilities.IndexWriter;
import utilities.JSONFileReader;

import java.util.HashMap;

/**
 * Contains methods to build and inverted index and write it to disk
 */
public class IndexBuilder {

    private InvertedIndex invertedIndex;

    public InvertedIndex getInvertedIndex() {
        return invertedIndex;
    }

    enum MAP_NAME  {
        SCENE_ID,
        PLAY_ID,
        DOC_LENGTH,
        DOC_TO_TERM
    }

    private static final String INVERTED_INDEX_FILE_NAME_COMPRESSED = "InvertedListCompressed";
    private static final String INVERTED_INDEX_FILE_NAME_UNCOMPRESSED = "InvertedList";
    private static final String LOOKUP_FILE_NAME_COMPRESSED =  "InvertedIndexLookupCompressed.txt";
    private static final String LOOKUP_FILE_NAME_UNCOMPRESSED =  "InvertedIndexLookupUncompressed.txt";
    private static final String SCENE_ID_MAP_FILE_NAME = "SceneIDMap.txt";
    private static final String PLAY_ID_MAP_FILE_NAME = "PlayIDMap.txt";
    private static final String DOC_LENGTH_MAP_FILE_NAME = "DocLengthMap.txt";
    private static final String DOC_TO_TERM_MAP_FILE_NAME = "DocTermMap.json";

    /**
     * @param args filename compress
     */
    public static void main(String[] args) {

        String filename = args[0];
        boolean toCompress = Boolean.parseBoolean(args[1]);
        IndexBuilder indexBuilder = new IndexBuilder();
        System.out.println("Starting the index building process with the following arguments:");
        System.out.println("Filename: "+filename);
        System.out.println("To Compress Index: "+toCompress);
        System.out.println("Building the index....");
        indexBuilder.buildIndex(filename);
        System.out.println("Index built successfully!");
        System.out.println("*******************************");
        String invertedIndexFileName = toCompress ? INVERTED_INDEX_FILE_NAME_COMPRESSED : INVERTED_INDEX_FILE_NAME_UNCOMPRESSED;
        String lookupFileName = toCompress ? LOOKUP_FILE_NAME_COMPRESSED : LOOKUP_FILE_NAME_UNCOMPRESSED;
        System.out.println("Writing the index to disk, with the following file names:");
        System.out.println("Inverted index filename: "+invertedIndexFileName);
        System.out.println("Lookup Map filename: "+lookupFileName);
        System.out.println("Scene ID Map  filename: "+SCENE_ID_MAP_FILE_NAME);
        System.out.println("Play ID Map filename: "+PLAY_ID_MAP_FILE_NAME);
        System.out.println("Doc Length Map filename: "+DOC_LENGTH_MAP_FILE_NAME);
        System.out.println("Doc Term Map filename: "+DOC_TO_TERM_MAP_FILE_NAME);
        System.out.println("Writing the files to disk......");
        indexBuilder.writeIndexToFile(lookupFileName, invertedIndexFileName, toCompress);
        indexBuilder.writeMapToFile(SCENE_ID_MAP_FILE_NAME, MAP_NAME.SCENE_ID);
        indexBuilder.writeMapToFile(PLAY_ID_MAP_FILE_NAME, MAP_NAME.PLAY_ID);
        indexBuilder.writeMapToFile(DOC_LENGTH_MAP_FILE_NAME, MAP_NAME.DOC_LENGTH);
        indexBuilder.writeMapToFile(DOC_TO_TERM_MAP_FILE_NAME, MAP_NAME.DOC_TO_TERM);
        System.out.println("Files written successfully");
        System.out.println("*******************************");
    }

    /**
     * Writes a HashMap to a file on disk
     * @param fileName The name of the output file
     * @param map The map to be written
     */
    public void writeMapToFile(String fileName, MAP_NAME map)
    {
        switch (map)
        {
            case SCENE_ID:
                IndexWriter<String> sceneWriter = new IndexWriter<>();
                sceneWriter.writeMap(fileName, this.invertedIndex.getSceneIDMap());
                break;
            case PLAY_ID:
                IndexWriter<String> playWriter = new IndexWriter<>();
                playWriter.writeMap(fileName, this.invertedIndex.getPlayIDMap());
                break;
            case DOC_LENGTH:
                IndexWriter<Integer> docLengthMapWriter = new IndexWriter<>();
                docLengthMapWriter.writeMap(fileName, this.invertedIndex.getDocLengthMap());
                break;
            case DOC_TO_TERM:
                IndexWriter<String> docTermMapWriter = new IndexWriter<>();
                docTermMapWriter.writeDocIndex(fileName, this.invertedIndex.getDocToTermMap());
        }
    }

    /**
     * Writes the inverted index and the lookup map to disk
     * @param lookupFileName The name of the lookup file
     * @param invertedFileName The name of the inverted index file
     * @param toCompress Whether to compress the index or not
     */
    public void writeIndexToFile(String lookupFileName, String invertedFileName, boolean toCompress)
    {
        IndexWriter<String> indexWriter = new IndexWriter<>();
        indexWriter.writeIndex(this.invertedIndex, lookupFileName, invertedFileName, toCompress);
    }

    /**
     * Builds the inverted index
     * @param inputFileName The name of the input file
     */
    public void buildIndex(String inputFileName)
    {
        this.invertedIndex = new InvertedIndex();

        JSONFileReader jsonFileReader = new JSONFileReader();
        JSONArray scenes = jsonFileReader.readFromJSONFile(inputFileName);
        for(int i=0; i<scenes.size(); i++)
        {
            JSONObject scene = (JSONObject) scenes.get(i);
            int docID = i+1;
            String sceneID = (String) scene.get("sceneId");
            this.invertedIndex.getSceneIDMap().put(docID, sceneID);
            String playID = (String) scene.get("playId");
            this.invertedIndex.getPlayIDMap().put(docID, playID);
            String text = (String) scene.get("text");
            String[] words = text.split("\\s+");
            this.invertedIndex.getDocLengthMap().put(docID, words.length);
            for (int pos = 0; pos < words.length; pos++) {
                String word = words[pos];
                this.invertedIndex.getIndex().putIfAbsent(word, new PostingList());
                this.invertedIndex.getIndex().get(word).add(docID, pos+1);
            }
            // Create a map from docID -> term, term freq
            HashMap<String, Integer> termFreqMap = new HashMap<>();
            for(String word : words){
                termFreqMap.putIfAbsent(word, 0);
                termFreqMap.put(word, termFreqMap.get(word)+1);
            }
            this.invertedIndex.getDocToTermMap().put(docID, termFreqMap);
        }
    }
}
