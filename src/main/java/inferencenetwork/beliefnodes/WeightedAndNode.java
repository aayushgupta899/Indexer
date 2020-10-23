package inferencenetwork.beliefnodes;

import inferencenetwork.BeliefNode;
import inferencenetwork.QueryNode;

import java.util.ArrayList;
import java.util.List;

public class WeightedAndNode extends BeliefNode {
    List<Double> weights;
    public WeightedAndNode(ArrayList<? extends QueryNode> c, List<Double> weights) {
        super(c);
        this.weights = weights;
    }
    @Override
    public Double score(Integer docID) {
        double score = 0.0;
        for(int i=0; i<children.size(); i++){
            score += weights.get(i) * children.get(i).score(docID);
        }
        return score;
    }
}
