package inferencenetwork.proximitynodes.windownodes;

import index.Index;
import index.Posting;
import inferencenetwork.ProximityNode;
import inferencenetwork.proximitynodes.WindowNode;
import retrieval.QLRetrievalModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UnorderedWindowNode extends WindowNode {

    private Integer windowSize;

    public UnorderedWindowNode(int windowSize, ArrayList<ProximityNode> termNodes, Index ind, QLRetrievalModel mod) {
        super(ind, mod);
        this.children = termNodes;
        this.windowSize = windowSize;
        generatePostings();
    }

    public Integer getWindowSize(Integer d){
        return windowSize == 0 ? index.getDocLength(d) : windowSize;
    }

    @Override
    protected Posting calculateWindows(ArrayList<Posting> postings) {
        int winSize = getWindowSize(postings.get(0).getDocID());
        int querySize = postings.size();
        Set<Integer> unvisited = new HashSet<>();
        Integer[] it = new Integer[querySize];
        Set<Integer> countedPos = new HashSet<Integer>();
        Posting p = null;

        for(int i=0; i<querySize; i++){
            it[i] = 0;
        }
        Set<Integer> pos;
        int i=0, s, d = postings.get(0).getDocID();
        while(!anyListDone(it, postings)){
            i = getMinIndex(it, postings);
            for(int j=0; j<querySize; j++){
                if(j != i){
                    unvisited.add(j);
                }
            }
            s = postings.get(i).getPositionsArray()[it[i]];
            pos = queryTermsInWindow(postings, unvisited, s+1, s+winSize);
            if(pos != null && pos.size() == postings.size()){
                boolean flag = true;
                for(Integer k : pos){
                    if(countedPos.contains(k)){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    if(p == null){
                        p = new Posting(d, s);
                    }
                    else{
                        p.getPositions().add(s);
                    }
                    countedPos.addAll(pos);
                }
            }
            unvisited.clear();
            it[i]++;
            if(pos != null){
                pos.clear();
            }
        }

        return p;
    }

    private boolean anyListDone(Integer[] it, ArrayList<Posting> postings) {
        for(int i = 0; i < it.length; i++){
            if (it[i] >= postings.get(i).getTermFrequency()) {
                return true;
            }
        }
        return false;
    }

    private int getMinIndex(Integer[] it, ArrayList<Posting> postings) {
        int temp, min = Integer.MAX_VALUE, min_index = -1;
        for (int i = 0; i < it.length; i++){
            temp = postings.get(i).getPositionsArray()[it[i]];
            if (min > temp) {
                min = temp;
                min_index = i;
            }
        }
        return min_index;
    }

    private Set<Integer> queryTermsInWindow(ArrayList<Posting> postings, Set<Integer> unvisited, Integer start, Integer end) {
        boolean found;
        Set<Integer> pos = new HashSet<Integer>();
        pos.add(start-1);
        for (Integer i : unvisited) {
            found = false;
            ArrayList<Integer> p = new ArrayList<>(Arrays.asList(postings.get(i).getPositionsArray()));
            for (int j = start; j < end; j++) {
                if (p.contains(j) && !pos.contains(j)) {
                    found = true;
                    pos.add(j);
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        return pos;
    }
}
