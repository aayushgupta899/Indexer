package inferencenetwork;

import index.InvertedIndex;

import java.io.IOException;
import java.io.RandomAccessFile;

public class PriorNode implements QueryNode {

    String priorFileName;
    InvertedIndex index;

    public PriorNode(String priorFileName, InvertedIndex index){
        this.priorFileName = priorFileName;
        this.index = index;
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
        score = this.index.retrievePrior(priorFileName, docID);
        return score;
    }
}
