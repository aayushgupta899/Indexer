package utilities;

import index.InvertedIndex;
import index.PostingList;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Defines methods to write index/maps to disk
 * @param <T> String/Integer depending on which map is being written
 */
public class IndexWriter<T> {

    /**
     * Writes an inverted index and lookup map to file
     * @param invertedIndex The inverted index to write to file
     * @param lookupFileName The name of lookup file
     * @param invertedFileName The name of the inverted index file
     * @param compress Whether to compress the index or not
     */
    public void writeIndex(InvertedIndex invertedIndex, String lookupFileName, String invertedFileName, boolean compress){
        long offset = 0;
        try (PrintWriter lookupWriter = new PrintWriter(lookupFileName, "UTF-8");
             RandomAccessFile invertedListWriter = new RandomAccessFile(invertedFileName, "rw")) {
            int count = 0;
            for (Map.Entry<String, PostingList> entry : invertedIndex.getIndex().entrySet()) {
                String term = entry.getKey();
                PostingList postings = entry.getValue();
                int docTermFrequency = postings.getDocumentCount();
                int collectionTermFrequency = postings.getTermFrequency();
                Integer[] posts = postings.toIntegerArray();
                ByteBuffer byteBuffer = ByteBuffer.allocate(posts.length * 8);
                if (compress) {
                    Compressor compressor = new Compressor();
                    compressor.compress(posts, byteBuffer);
                } else {
                    for (int i : posts) {
                        byteBuffer.putInt(i);
                    }
                }
                byte[] array = byteBuffer.array();
                invertedListWriter.write(array, 0, byteBuffer.position());
                long bytesWritten = invertedListWriter.getFilePointer() - offset;
                lookupWriter.println(term + " " + offset + " " + bytesWritten + " " + docTermFrequency + " " + collectionTermFrequency);
                offset = invertedListWriter.getFilePointer();
                count++;
            }
            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a map to disk
     * @param filename The name of the file
     * @param map The map
     */
    public void writeMap(String filename, Map<Integer, T> map)
    {
        List<String> lines = new ArrayList<>();
        map.forEach((k,v) -> lines.add(k.toString() + " " + v.toString()));
        try{
            Path file = Paths.get(filename);
            Files.write(file, lines, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
