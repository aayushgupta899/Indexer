package api;

import index.InvertedIndex;
import index.PostingList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utilities.IndexWriter;
import utilities.JSONFileReader;

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
        DOC_LENGTH
    }

    private static final String INVERTED_INDEX_FILE_NAME_COMPRESSED = "InvertedListCompressed";
    private static final String INVERTED_INDEX_FILE_NAME_UNCOMPRESSED = "InvertedList";
    private static final String LOOKUP_FILE_NAME_COMPRESSED =  "InvertedIndexLookupCompressed.txt";
    private static final String LOOKUP_FILE_NAME_UNCOMPRESSED =  "InvertedIndexLookupUncompressed.txt";
    private static final String SCENE_ID_MAP_FILE_NAME = "SceneIDMap.txt";
    private static final String PLAY_ID_MAP_FILE_NAME = "PlayIDMap.txt";
    private static final String DOC_LENGTH_MAP_FILE_NAME = "DocLengthMap.txt";

    /**
     * @param args filename compress
     */
    public static void main(String[] args) {

        String filename = args[0];
        boolean toCompress = Boolean.parseBoolean(args[1]);
        IndexBuilder indexBuilder = new IndexBuilder();
        indexBuilder.buildIndex(filename);
        String invertedIndexFileName = toCompress ? INVERTED_INDEX_FILE_NAME_COMPRESSED : INVERTED_INDEX_FILE_NAME_UNCOMPRESSED;
        String lookupFileName = toCompress ? LOOKUP_FILE_NAME_COMPRESSED : LOOKUP_FILE_NAME_UNCOMPRESSED;
        indexBuilder.writeIndexToFile(lookupFileName, invertedIndexFileName, toCompress);
        indexBuilder.writeMapToFile(SCENE_ID_MAP_FILE_NAME, MAP_NAME.SCENE_ID);
        indexBuilder.writeMapToFile(PLAY_ID_MAP_FILE_NAME, MAP_NAME.PLAY_ID);
        indexBuilder.writeMapToFile(DOC_LENGTH_MAP_FILE_NAME, MAP_NAME.DOC_LENGTH);
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
        }
    }
}
