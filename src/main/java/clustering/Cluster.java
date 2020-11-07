package clustering;

import index.InvertedIndex;

import java.util.*;

public class Cluster {

    List<Integer> documentIDs;
    Map<String, Double> clusterRep;
    CosineSimilarity sim;
    Linkage linkage;
    InvertedIndex index;
    int id;

    public Cluster(int id, InvertedIndex index, CosineSimilarity sim, Linkage linkage){
        documentIDs = new ArrayList<>();
        this.index = index;
        this.id = id;
        this.sim = sim;
        clusterRep = new HashMap<>();
        this.linkage = linkage;
    }

    public void add(int docID){
        Map<String, Integer> termMap = this.index.getDocumentVector(docID);
        Set<String> union = new HashSet<>();
        for(String term : termMap.keySet()){
            union.add(term);
        }
        for(String term : clusterRep.keySet()){
            union.add(term);
        }
        for(String term : union){
            if(!clusterRep.containsKey(term)){
                clusterRep.put(term, (double)termMap.get(term)/(this.documentIDs.size() + 1));
            }
            else{
                double curAverage = clusterRep.get(term);
                double sum = curAverage * this.documentIDs.size();
                sum += termMap.getOrDefault(term, 0);
                double newAverage = sum / (this.documentIDs.size() + 1);
                clusterRep.put(term, newAverage);
            }
        }
        this.documentIDs.add(docID);
    }
    public List<Integer> getDocumentIDs(){
        return this.documentIDs;
    }
    public int getID(){
        return this.id;
    }

    public Double score(Map<String, Integer> other){

        Map<String, Double> otherDouble = new HashMap<>();
        for(Map.Entry<String, Integer> entry : other.entrySet()){
            otherDouble.put(entry.getKey(), (double)entry.getValue());
        }
        switch(this.linkage){
            case SINGLE_LINK:
                return scoreSingle(otherDouble);
            case MEAN_LINK:
                return scoreMean(otherDouble);
            case COMPLETE_LINK:
                return scoreComplete(otherDouble);
            case AVERAGE_LINK:
                return scoreAverage(otherDouble);
            default:
                return 0.0;
        }
    }
    private Double scoreAverage(Map<String, Double> other){
        double sum = 0.0;
        for(int docID : documentIDs){
            Map<String, Integer> documentVector = this.index.getDocumentVector(docID);
            Map<String, Double> documentVectorDouble = new HashMap<>();
            for(Map.Entry<String, Integer> entry : documentVector.entrySet()){
                documentVectorDouble.put(entry.getKey(), (double)entry.getValue());
            }
            double score = sim.getCosineSimilarity(documentVectorDouble, other);
           sum += score;
        }
        return sum / documentIDs.size();
    }
    private Double scoreComplete(Map<String, Double> other){
        double minScore = Double.MAX_VALUE;
        for(int docID : documentIDs){
            Map<String, Integer> documentVector = this.index.getDocumentVector(docID);
            Map<String, Double> documentVectorDouble = new HashMap<>();
            for(Map.Entry<String, Integer> entry : documentVector.entrySet()){
                documentVectorDouble.put(entry.getKey(), (double)entry.getValue());
            }
            double score = sim.getCosineSimilarity(documentVectorDouble, other);
            if(score < minScore)
            {
                minScore = score;
            }
        }
        return minScore;
    }
    private Double scoreMean(Map<String, Double> other){

        return sim.getCosineSimilarity(this.clusterRep, other);
    }
    private Double scoreSingle(Map<String, Double> other){
        double maxScore = 0.0;
        for(int docID : documentIDs){
            Map<String, Integer> documentVector = this.index.getDocumentVector(docID);
            Map<String, Double> documentVectorDouble = new HashMap<>();
            for(Map.Entry<String, Integer> entry : documentVector.entrySet()){
                documentVectorDouble.put(entry.getKey(), (double)entry.getValue());
            }
            double score = sim.getCosineSimilarity(documentVectorDouble, other);
            if(score > maxScore)
            {
                maxScore = score;
            }
        }
        return maxScore;
    }

    public Map<String, Double> getClusterRep() {
        return clusterRep;
    }
}
