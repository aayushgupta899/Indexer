package inferencenetwork.beliefnodes;

import inferencenetwork.BeliefNode;
import inferencenetwork.QueryNode;

import java.util.ArrayList;
import java.util.List;

public class WeightedSumNode extends BeliefNode {
    List<Double> weights;
    public WeightedSumNode(ArrayList<? extends QueryNode> c, List<Double> weights) {
        super(c);
        this.weights = weights;
    }

    @Override
    public Double score(Integer docID) {
        Double sum = 0.0;
        for(int i=0; i<children.size(); i++){
            sum += weights.get(i) * Math.exp(children.get(i).score(docID));
        }
        double weightsSum = weights.stream().mapToDouble(c -> c).sum();
        return sum / weightsSum;
    }
}
