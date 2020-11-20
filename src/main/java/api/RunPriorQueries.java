package api;

import index.InvertedIndex;
import inferencenetwork.InferenceNetwork;
import inferencenetwork.PriorNode;
import inferencenetwork.ProximityNode;
import inferencenetwork.QueryNode;
import inferencenetwork.beliefnodes.AndNode;
import inferencenetwork.proximitynodes.TermNode;
import retrieval.QLRetrievalModel;
import retrieval.QueryLikelihoodDirModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunPriorQueries {

    private static final String QUERY_FILE_NAME = "queries.txt";
    private static final int MU = 1500;

    /**
     * @param args priorFileName compress k
     */
    public static void main(String[] args) {

        String priorFileName = args[0];
        String priorType = priorFileName.split("\\.")[0];
        String outputFileName = priorType + ".trecrun";
        boolean compress = Boolean.parseBoolean(args[1]);
        int k = Integer.parseInt(args[2]);

        InvertedIndex index = new InvertedIndex();
        index.load(compress);
        InferenceNetwork inferenceNetwork = new InferenceNetwork();
        QLRetrievalModel retrievalModel = new QueryLikelihoodDirModel(MU, index.getCollectionSize());

        String runID = "aayushgupta-"+priorType+"-and-dir-"+MU;
        try(BufferedReader reader = new BufferedReader(new FileReader(QUERY_FILE_NAME));
            FileWriter myWriter = new FileWriter(outputFileName);){
            String query = "";
            int queryIndex = 1;
            while((query = reader.readLine()) != null) {
                ArrayList<QueryNode> children = new ArrayList<>();

                String[] terms = query.split("\\s+");
                for (String term : terms) {
                    ProximityNode node = new TermNode(index, retrievalModel, term);
                    children.add(node);
                }

                children.add(new PriorNode(priorFileName, index));

                QueryNode queryNode = new AndNode(children);
                List<Map.Entry<Integer, Double>> results = inferenceNetwork.runQuery(queryNode, k);

                String queryID = "Q" + queryIndex;
                int rank = 1;
                for (Map.Entry<Integer, Double> entry : results) {
                    String sceneID = index.getDocName(entry.getKey());
                    String resultLine = queryID + " skip "+sceneID+" "+rank+" "+entry.getValue()+" "+runID+"\n";
                    myWriter.write(resultLine);
                    rank++;
                }
                queryIndex++;
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }


    }
}
