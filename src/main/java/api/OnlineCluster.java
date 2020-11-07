package api;

import clustering.Cluster;
import clustering.CosineSimilarity;
import clustering.Linkage;
import index.InvertedIndex;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnlineCluster {

    /**
     * @param args compress linkage
     */
    public static void main(String[] args) {

        boolean compress = Boolean.parseBoolean(args[0]);
        Linkage linkage = Linkage.valueOf(args[1]);
        InvertedIndex index = new InvertedIndex();
        index.load(compress);
        List<Double> thresholdValues=  new ArrayList<>();
        CosineSimilarity sim = new CosineSimilarity(index);
        for(double i=0.05; i<1; i+=0.05){
            thresholdValues.add(i);
        }
        for(double threshold : thresholdValues){
            List<Cluster> clusters = new ArrayList<>();
            int currentClusterID = 0;
            for(int docID=1; docID<=index.getDocCount(); docID++){
                Map<String, Integer> documentVector = index.getDocumentVector(docID);
                Double bestScore = 0.0;
                Cluster bestCluster = null;
                for(Cluster cluster : clusters){
                    double score = cluster.score(documentVector);
                    if(score > bestScore){
                        bestScore = score;
                        bestCluster = cluster;
                    }
                }
                if(bestScore > threshold){
                    bestCluster.add(docID);
                }
                else{
                    Cluster newCluster = new Cluster(++currentClusterID, index, sim, linkage);
                    newCluster.add(docID);
                    clusters.add(newCluster);
                }
            }
            String filename = "cluster-"+String.format("%.2f", threshold)+".out";
            try (FileWriter file = new FileWriter(filename)) {
                for(Cluster cluster : clusters){
                    List<Integer> documentIDs = cluster.getDocumentIDs();
                    for(int docID : documentIDs){
                        String sceneID = index.getSceneIDMap().get(docID);
                        String line = cluster.getID() + "\t" + sceneID+"\n";
                        file.write(line);
                    }
                }
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
