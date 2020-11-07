package clustering;

import index.InvertedIndex;

import java.util.Map;

public class CosineSimilarity {

    InvertedIndex index;

    public CosineSimilarity(InvertedIndex index){
        this.index = index;
    }

    public Double getCosineSimilarity(Map<String, Double> map1, Map<String, Double> map2){
        double numerator = 0.0;
        double N = index.getDocCount();
        for(Map.Entry<String, Double> entry : map1.entrySet()){
            String term1 = entry.getKey();
            double count1 = entry.getValue();
            if(!map2.containsKey(term1)){
                continue;
            }
            double n1 = index.getDocFrequency(term1);
            double logIDF = Math.log(((N+1)/n1)+0.5);
            numerator += count1 * logIDF * map2.get(term1) * logIDF;
        }
        double denominatorA = 0.0, denominatorB = 0.0;
        for(Map.Entry<String, Double> entry : map1.entrySet()) {
            double n1 = index.getDocFrequency(entry.getKey());
            double logIDF = Math.log(((N+1)/n1)+0.5);
            denominatorA += entry.getValue() * entry.getValue() * logIDF * logIDF;
        }
        denominatorA = Math.sqrt(denominatorA);
        for(Map.Entry<String, Double> entry : map2.entrySet()) {
            double n1 = index.getDocFrequency(entry.getKey());
            double logIDF = Math.log(((N+1)/n1)+0.5);
            denominatorB += entry.getValue() * entry.getValue() * logIDF * logIDF;
        }
        denominatorB = Math.sqrt(denominatorB);
        return numerator / (denominatorA * denominatorB);
    }
}
