package utilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class Compressor {

    public static void main(String[] args) {
        Integer[] a = {1, 2, 3,4, 2, 3, 2,4,5, 3, 6, 1,2,3,4,5,6};
        System.out.println("Input:" +Arrays.toString(a));
        ByteBuffer byteBuffer = ByteBuffer.allocate(a.length);
        Compressor compressor = new Compressor();
        compressor.compress(a, byteBuffer);
        byte[] arr = byteBuffer.array();
        IntBuffer output = IntBuffer.allocate(a.length);
        compressor.decompress(arr, output);
        System.out.println("Output: "+Arrays.toString(output.array()));
    }

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
