package utilities;

import index.Posting;
import index.PostingList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Defines methods to read inverted index and maps from files on disk
 */
public class IndexReader {

    /**
     * Reads an inverted index from disk
     * @param invertedFileName The name of the inverted index file
     * @param lookupMap The lookup map
     * @param isCompressed Whether the index is compressed or not
     * @return The inverted index
     */
    public Map<String, PostingList> readIndex(String invertedFileName, Map<String, List<String>> lookupMap, boolean isCompressed)
    {
        Map<String, PostingList> invertedIndex = null;
        try (RandomAccessFile invertedListReader = new RandomAccessFile(invertedFileName, "rw");){
            invertedIndex = new HashMap<>();
            int count = 0;
            for(Map.Entry<String, List<String>> entry : lookupMap.entrySet())
            {
                String term = entry.getKey();
                long offset = Long.parseLong(entry.getValue().get(0));
                int buffLength = Integer.parseInt(entry.getValue().get(1));
                byte[] buffer = new byte[buffLength];
                invertedListReader.seek(offset);
                invertedListReader.read(buffer, 0, buffLength);
                PostingList postings = new PostingList();
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
                invertedIndex.put(term, postings);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invertedIndex;
    }

    /**
     * Reads a map from disk. Useful for getting sceneIDMap, playIDMap
     * @param filename The name of the file on disk
     * @return The map
     */
    public Map<Integer, String> readStringMap(String filename)
    {
        Map<Integer, String> result = null;
        try(BufferedReader reader=  new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));) {
            result = new HashMap<>();
            String currentLine = reader.readLine();
            while(currentLine != null)
            {
                String[] data = currentLine.split("\\s+");
                int key = Integer.parseInt(data[0]);
                String value = data[1];
                result.put(key, value);
                currentLine = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Reads a map from disk. Useful for getting docLengthMap
     * @param filename The name of the file on disk
     * @return The map
     */
    public Map<Integer, Integer> readIntegerMap(String filename)
    {
        Map<Integer, Integer> result = null;
        try(BufferedReader reader =  new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));) {
            result = new HashMap<>();
            String currentLine = reader.readLine();
            while(currentLine != null)
            {
                String[] data = currentLine.split("\\s+");
                int key = Integer.parseInt(data[0]);
                int value = Integer.parseInt(data[1]);
                result.put(key, value);
                currentLine = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Converts a byte array to int
     * @param bytes The input byte array
     * @return The integer corresponding to the byte array
     */
    private int fromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Reads the lookup map from a file on disk
     * @param lookupFileName The name of the lookup file
     * @return The lookup map
     */
    public Map<String, List<String>> readLookup(String lookupFileName)
    {
        Map<String, List<String>> result = null;
        try(BufferedReader lookupReader =  new BufferedReader(new InputStreamReader(new FileInputStream(lookupFileName), StandardCharsets.UTF_8));) {
            result = new HashMap<>();
            String currentLine = lookupReader.readLine();
            while(currentLine != null)
            {
                String[] lookupData = currentLine.split("\\s+");
                String term = lookupData[0];
                List<String> data = new ArrayList<>();
                data.add(lookupData[1]);
                data.add(lookupData[2]);
                data.add(lookupData[3]);
                data.add(lookupData[4]);
                result.put(term, data);
                currentLine = lookupReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<Integer, Map<String, Integer>> readDocTermMap(String filename){
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        HashMap<Integer, Map<String, Integer>> result = new HashMap<>();
        try (FileReader reader = new FileReader(filename))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray docList = (JSONArray) obj;

            for(int i=0; i<docList.size(); i++){
                JSONObject docEntry = (JSONObject) docList.get(i);
                HashMap<String, Integer> termMap = new HashMap<>();
                int docID = (int)(long)(docEntry.get("docID"));
                JSONArray termList = (JSONArray) docEntry.get("termArray");
                for(int j=0; j<termList.size(); j++){
                    JSONObject termEntry = (JSONObject) termList.get(j);
                    String term = (String) termEntry.get("term");
                    int count = (int)(long)(termEntry.get("count"));
                    termMap.put(term, count);
                }
                result.put(docID, termMap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


}
