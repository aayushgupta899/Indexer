package inferencenetwork.beliefnodes;

import inferencenetwork.BeliefNode;
import inferencenetwork.QueryNode;

import java.util.ArrayList;

public class MaxNode extends BeliefNode {
    public MaxNode(ArrayList<? extends QueryNode> c) {
        super(c);
    }

    @Override
    public Double score(Integer docID) {

        Double max = children.get(0).score(docID);
        for(int i=0; i<children.size(); i++){
            double score = children.get(i).score(docID);
            if(score > max){
                max = score;
            }
        }
        return max;
    }
}
