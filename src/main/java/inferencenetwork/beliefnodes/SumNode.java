package inferencenetwork.beliefnodes;

import inferencenetwork.BeliefNode;
import inferencenetwork.QueryNode;

import java.util.ArrayList;

public class SumNode extends BeliefNode {
    public SumNode(ArrayList<? extends QueryNode> c) {
        super(c);
    }
    @Override
    public Double score(Integer docID) {
        Double sum = 0.0;
        for(int i=0; i<children.size(); i++){
            sum += Math.exp(children.get(i).score(docID));
        }
        return Math.log(sum)/children.size();
    }
}
