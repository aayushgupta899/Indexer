package inferencenetwork;

import index.Index;
import index.InvertedIndex;
import inferencenetwork.beliefnodes.AndNode;
import inferencenetwork.beliefnodes.MaxNode;
import inferencenetwork.beliefnodes.OrNode;
import inferencenetwork.beliefnodes.SumNode;
import inferencenetwork.proximitynodes.TermNode;
import inferencenetwork.proximitynodes.windownodes.OrderedWindowNode;
import inferencenetwork.proximitynodes.windownodes.UnorderedWindowNode;
import retrieval.QLRetrievalModel;
import retrieval.QueryLikelihoodDirModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestInferenceNetwork {
    /**
     * @param args k compressed queryFile
     */
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        boolean compressed = Boolean.parseBoolean(args[1]);
        Index invertedIndex = new InvertedIndex();
        invertedIndex.load(compressed);
        long collectionSize = invertedIndex.getCollectionSize();

        QLRetrievalModel model = new QueryLikelihoodDirModel(1500, collectionSize);
        List<Map.Entry<Integer, Double>> results;
        InferenceNetwork network = new InferenceNetwork();
        QueryNode queryNode;
        ArrayList<ProximityNode> children;

        String queryFile = args[2];
        List<String> queries = new ArrayList<String>();
        try {
            String query;

            BufferedReader reader = new BufferedReader(new FileReader(queryFile));
            while ((query = reader.readLine()) != null) {
                queries.add(query);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String outFile, runID, qID;
        int qNum = 0;

        //unordered
        outFile = "uw.trecrun";
        runID = "aayushgupta-uw3-dir-1500";
        qNum = 0;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile));){
            for (String query : queries) {
                qNum++;
                children = genTermNodes(query, invertedIndex, model);
                int winSize = 3 * children.size();
                queryNode = new UnorderedWindowNode(winSize, children, invertedIndex, model);
                results = network.runQuery(queryNode, k);
                qID = "Q" + qNum;
                printResults(results, invertedIndex, writer, runID, qID);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //ordered
        outFile = "od1.trecrun";
        runID = "aayushgupta-od1-dir-1500";
        qNum = 0;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile));){
            for (String query : queries) {
                qNum++;
                children = genTermNodes(query, invertedIndex, model);
                int winSize = 1;
                queryNode = new OrderedWindowNode(winSize, children, invertedIndex, model);
                results = network.runQuery(queryNode, k);
                qID = "Q" + qNum;
                printResults(results, invertedIndex, writer, runID, qID);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //and
        outFile = "and.trecrun";
        runID = "aayushgupta-and-dir-1500";
        qNum = 0;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile));){
            for (String query : queries) {
                qNum++;
                children = genTermNodes(query, invertedIndex, model);
                queryNode = new AndNode(children);
                results = network.runQuery(queryNode, k);
                qID = "Q" + qNum;
                boolean append = qNum > 1;
                printResults(results, invertedIndex, writer, runID, qID);
                }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // or
        outFile = "or.trecrun";
        runID = "aayushgupta-or-dir-1500";
        qNum = 0;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile));){
            for (String query : queries) {
                qNum++;
                children = genTermNodes(query, invertedIndex, model);
                queryNode = new OrNode(children);
                results = network.runQuery(queryNode, k);
                qID = "Q" + qNum;
                printResults(results, invertedIndex, writer, runID, qID);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // sum
        outFile = "sum.trecrun";
        runID = "aayushgupta-sum-dir-1500";
        qNum = 0;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile));){
            for (String query : queries) {
                qNum++;
                children = genTermNodes(query, invertedIndex, model);
                queryNode = new SumNode(children);
                results = network.runQuery(queryNode, k);
                qID = "Q" + qNum;
                printResults(results, invertedIndex, writer, runID, qID);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // max
        outFile = "max.trecrun";
        runID = "aayushgupta-max-dir-1500";
        qNum = 0;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile));){
            for (String query : queries) {
                qNum++;
                children = genTermNodes(query, invertedIndex, model);
                queryNode = new MaxNode(children);
                results = network.runQuery(queryNode, k);
                qID = "Q" + qNum;
                printResults(results, invertedIndex, writer, runID, qID);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static ArrayList<ProximityNode> genTermNodes(String query, Index index, QLRetrievalModel model){
        String[] terms = query.split("\\s+");
        ArrayList<ProximityNode> children = new ArrayList<ProximityNode>();
        for(String term : terms){
            ProximityNode node = new TermNode(index, model, term);
            children.add(node);
        }
        return children;
    }

    private static void printResults(List<Map.Entry<Integer, Double>> results, Index index, PrintWriter writer,
                                     String runID, String qID){
        int rank = 1;
        for(Map.Entry<Integer, Double> entry : results){
            String sceneID = index.getDocName(entry.getKey());
            String resultLine = qID + " skip " + sceneID + " " + rank + " " + String.format("%.7f", entry.getValue())
                    + " " + runID;
            writer.println(resultLine);
            rank++;
        }
    }
}
