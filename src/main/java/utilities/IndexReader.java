package utilities;

import index.InvertedIndex;
import index.Posting;
import index.PostingList;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexReader {

    public Map<String, PostingList> readIndex(String lookupFileName, String invertedFileName, boolean isCompressed)
    {
        Map<String, PostingList> invertedIndex = null;
        try (RandomAccessFile invertedListReader = new RandomAccessFile(invertedFileName, "rw");
             BufferedReader lookupReader =  new BufferedReader(new InputStreamReader(new FileInputStream(lookupFileName), StandardCharsets.UTF_8));){
            invertedIndex = new HashMap<>();
            String currentLine = lookupReader.readLine();
            int count = 0;
            while(currentLine != null)
            {
                String[] lookupData = currentLine.split("\\s+");
                String term = lookupData[0];
                long offset = Long.parseLong(lookupData[1]);
                int buffLength = Integer.parseInt(lookupData[2]);
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
                currentLine = lookupReader.readLine();
                count++;
            }
            System.out.println("Terms in file:"+count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invertedIndex;
    }

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
    private int fromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

}
