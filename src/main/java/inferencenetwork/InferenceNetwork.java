package inferencenetwork;

import java.util.*;

public class InferenceNetwork {
    public List<Map.Entry<Integer, Double>> runQuery(QueryNode queryNode, int k){
        PriorityQueue<Map.Entry<Integer, Double>> result = new PriorityQueue<>(Map.Entry.<Integer, Double>comparingByValue());
        while(queryNode.hasMore()){
            Integer d = queryNode.nextCandidate();
            queryNode.skipTo(d);
            Double curScore = queryNode.score(d);
            if(curScore != null){
                result.add(new AbstractMap.SimpleEntry<Integer, Double>(d, curScore));
                if(result.size() > k){
                    result.poll();
                }
            }
            queryNode.skipTo(d+1);
        }
        ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<>();
        scores.addAll(result);
        scores.sort(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder()));
        return scores;
    }
}
