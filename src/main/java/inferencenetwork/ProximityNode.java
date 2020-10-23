package inferencenetwork;

import index.Index;
import index.Posting;
import index.PostingList;
import retrieval.QLRetrievalModel;

public abstract class ProximityNode implements QueryNode {
    protected int ctf = 0;
    protected int curDocItr = 0;
    protected PostingList postingList = null;
    protected Index index;
    protected QLRetrievalModel model;
    public ProximityNode(Index ind, QLRetrievalModel mod){
        this.index = ind;
        this.model = mod;
    }
    protected abstract void generatePostings();
    public boolean hasMore() {
        return postingList.hasMore();
    }
    public Integer nextCandidate() {
        if (postingList.hasMore()) {
            return postingList.getCurrentPosting().getDocID();
        }
        return index.getDocCount() + 1;
    }
    public void skipTo(int docId) {
       postingList.skipTo(docId);
    }

    public Double score(Integer docId) {
        int tf = 0;
        if (postingList.hasMore() && postingList.getCurrentPosting().getDocID() == docId) {
            tf = postingList.getCurrentPosting().getTermFrequency();
        }
        Double score = model.scoreOccurrence(tf, ctf, index.getDocLength(docId));
        return score;
    }

    public Posting getCurrentPosting(){
        return postingList.getCurrentPosting();
    }

}
