package inferencenetwork.proximitynodes;

import index.Index;
import index.Posting;
import index.PostingList;
import inferencenetwork.ProximityNode;
import retrieval.QLRetrievalModel;

import java.util.ArrayList;

public abstract class WindowNode extends ProximityNode {

    public ArrayList<ProximityNode> children;

    public WindowNode(Index ind, QLRetrievalModel mod) {
        super(ind, mod);
    }

    abstract protected Posting calculateWindows(ArrayList<Posting> matchingPostings);

    private boolean allHaveMore(){
        return children.stream().allMatch(ProximityNode::hasMore);
    }

    private int candidate(){
        return children.stream().mapToInt(ProximityNode::nextCandidate).max().getAsInt();
    }

    @Override
    protected void generatePostings(){
        postingList = new PostingList();
        postingList.startIteration();
        ArrayList<Posting> matchingPostings = new ArrayList<>();
        while(allHaveMore()){
            Integer next = candidate();
            children.forEach(c -> c.skipTo(next));
            if(children.stream().allMatch(c -> next.equals(c.nextCandidate()))){
                for(ProximityNode child : children){
                    matchingPostings.add(child.getCurrentPosting());
                }
                Posting p = calculateWindows(matchingPostings);
                if(p != null){
                    postingList.add(p);
                    ctf += p.getTermFrequency();
                }
            }
            matchingPostings.clear();
            children.forEach(c -> c.skipTo(next +1));
        }
        postingList.startIteration();
    }
}
