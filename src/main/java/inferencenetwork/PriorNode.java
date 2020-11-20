package inferencenetwork;

import index.InvertedIndex;

import java.io.IOException;
import java.io.RandomAccessFile;

public class PriorNode implements QueryNode {

    String priorFileName;

    public PriorNode(String priorFileName){
        this.priorFileName = priorFileName;
    }

    @Override
    public Integer nextCandidate() {
        return null;
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public void skipTo(int docID) {

    }

    @Override
    public Double score(Integer docID) {
        Double score = null;
        score = retrievePrior(priorFileName, docID);
        return score;
    }

    public Double retrievePrior(String fileName, int docID){
        Double result = null;
        try(RandomAccessFile reader = new RandomAccessFile(fileName, "rw")){
            reader.seek((docID-1)*8);
            result = reader.readDouble();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
