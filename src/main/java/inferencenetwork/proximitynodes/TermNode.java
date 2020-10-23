package inferencenetwork.proximitynodes;

import index.Index;
import inferencenetwork.ProximityNode;
import retrieval.QLRetrievalModel;

public class TermNode extends ProximityNode {

    String term;

    public TermNode(Index ind, QLRetrievalModel mod, String term) {
        super(ind, mod);
        this.term = term;
        generatePostings();
    }

    @Override
    protected void generatePostings() {
        postingList = index.getPostingList(this.term);
        ctf = index.getTermFrequency(term);
    }
}
