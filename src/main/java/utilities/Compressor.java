package utilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Defines methods for VByte Encoding/Decoding with Delta Compression/Decompression
 */
public class Compressor {

    /**
     * Encodes an array using Delta encoding
     * @param input The input array
     */
    public void deltaEncode(Integer[] input){
        int idx = 0;
        int savedDocID = 0;
        int savedPos = 0;
        int firstPos = 0;
        int firstDocID = input[idx++];
        while(idx < input.length){
            int count = input[idx++];
            try{
                savedDocID = input[idx+count];
                input[idx+count] -= firstDocID;
                firstDocID = savedDocID;
            } catch (ArrayIndexOutOfBoundsException e)
            {
                //Ignore this
            }
            firstPos = input[idx];
            for(int j=1; j<count; j++)
            {
                savedPos = input[idx+j];
                input[idx+j] -= firstPos;
                firstPos = savedPos;
            }
            idx += count + 1;
        }

    }

    /**
     * Decodes an array which was encoded using Delta encoding
     * @param output The buffer which contains delta encoded data
     */
    public void deltaDecode(IntBuffer output){
        int[] outputArray = output.array();
        int idx = 0;
        while(idx < output.position())
        {
            int firstDocID = outputArray[idx++];
            int count = outputArray[idx++];
            try{
                outputArray[idx+count] += firstDocID;
            } catch (ArrayIndexOutOfBoundsException e)
            {
                //Ignore this
            }
            int firstPos = outputArray[idx];
            for(int j=1; j<count; j++)
            {
                outputArray[idx+j] += firstPos;
                firstPos = outputArray[idx+j];
            }
            idx += count;
        }
    }

    /**
     * Compresses an array using Delta encoding and VByte compression
     * @param input The input array to compress
     * @param output The buffer to write the output to
     */
    public void compress(Integer[] input, ByteBuffer output)
    {
        deltaEncode(input);
        for(int i : input)
        {
            while(i >= 128)
            {
                output.put((byte)(i & 0x7f));
                i >>>= 7;
            }
            output.put((byte)(i | 0x80));
        }
    }

    /**
     * Decompresses an array which was compressed using VByte Compression
     * and encoded using Delta encoding
     * @param input The input which is to be decompressed
     * @param output The output to write to
     */
    public void decompress(byte[] input, IntBuffer output)
    {
        for(int i=0; i<input.length; i++)
        {
            int pos = 0;
            int result = ((int) input[i] & 0x7F);
            while((input[i] & 0x80) == 0)
            {
                i++;
                pos++;
                int unsignedByte = (int) input[i] & 0x7F;
                result |= (unsignedByte << (7 * pos));
            }
            output.put(result);
        }
        deltaDecode(output);
    }
}
