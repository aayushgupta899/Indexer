package inferencenetwork.proximitynodes.windownodes;

import index.Index;
import index.Posting;
import inferencenetwork.ProximityNode;
import inferencenetwork.proximitynodes.WindowNode;
import retrieval.QLRetrievalModel;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderedWindowNode extends WindowNode {

    private Integer windowSize;

    public OrderedWindowNode(int windowSize, ArrayList<ProximityNode> termNodes, Index ind, QLRetrievalModel mod) {
        super(ind, mod);
        this.children = termNodes;
        this.windowSize = windowSize;
        generatePostings();
    }

    @Override
    protected Posting calculateWindows(ArrayList<Posting> matchingPostings) {
        int prev;
        boolean flag = false;
        Posting posting = null;
        if (matchingPostings.size() == 1) {
            return matchingPostings.get(0);
        }
        Integer[] firstPosting = matchingPostings.get(0).getPositionsArray();

        for(int i = 0; i < firstPosting.length; i++){
            prev = firstPosting[i];
            for (int j = 1; j < matchingPostings.size(); j++){
                ArrayList<Integer> p = new ArrayList<>(Arrays.asList( matchingPostings.get(j).getPositionsArray()));
                flag = false;
                for (int k = 0; k < p.size(); k++) {
                    int current = p.get(k);
                    if (prev < current && current <= prev + windowSize) {
                        flag = true;
                        prev = current;
                        break;
                    }
                }
                if (!flag) {
                    break;
                }
            }
            if (flag) {
                if( posting == null){
                    posting =  new Posting(matchingPostings.get(0).getDocID(), firstPosting[i]);
                }
                else{
                    posting.getPositions().add(firstPosting[i]);
                }
            }
        }
        return posting;
    }


}
