package api;

import index.InvertedIndex;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratePriors {

    /**
     * @param args compress
     */
    public static void main(String[] args) {
        InvertedIndex index = new InvertedIndex();
        boolean compress = Boolean.parseBoolean(args[0]);
        index.load(compress);

        List<Double> uniformPriorList = new ArrayList<>();
        for(int docID=1; docID<=index.getDocCount(); docID++){
            Double score = Math.log(1.0/index.getDocCount());
            uniformPriorList.add(score);
        }
        try(RandomAccessFile writer = new RandomAccessFile("uniform.prior", "rw")){
            for(int i=0; i<uniformPriorList.size(); i++) {
                writer.writeDouble(uniformPriorList.get(i));
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        List<Double> randomPriorList = new ArrayList<>();
        Random random = new Random();
        for(int docID=1; docID<=index.getDocCount(); docID++){
            Double score = random.nextDouble();
            randomPriorList.add(score);
        }
        try(RandomAccessFile writer = new RandomAccessFile("random.prior", "rw")){
            for(int i=0; i<randomPriorList.size(); i++) {
                writer.writeDouble(randomPriorList.get(i));
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
