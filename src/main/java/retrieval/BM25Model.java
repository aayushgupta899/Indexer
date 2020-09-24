package retrieval;

import index.InvertedIndex;
import index.Posting;
import index.PostingList;

import java.util.*;

public class BM25Model implements RetrievalModel {

    private double k1;
    private double k2;
    private double b;
    private double rI;
    private double R;

    public BM25Model(double k1, double k2, double b, double rI, double R){
        this.k1 = k1;
        this.k2 = k2;
        this.b = b;
        this.rI = rI;
        this.R = R;
    }
    @Override
    public List<Map.Entry<Integer, Double>> retrieveQuery(InvertedIndex index, String[] queryTerms, int k, boolean compress) {
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        index.getQueryPostings(compress, queryTerms);
        Map<String, Integer> queryTermCounts = new HashMap<>();
        for(String query : queryTerms){
            queryTermCounts.putIfAbsent(query, 0);
            queryTermCounts.put(query, queryTermCounts.get(query)+1);
        }
        Map<String, PostingList> postingLists = new HashMap<>();
        for(Map.Entry<String, Integer> entry : queryTermCounts.entrySet()) {
            postingLists.put(entry.getKey(), index.getPostings(entry.getKey()));
        }
        for(int doc=1; doc<=index.getDocCount(); doc++)
        {
            Double score = 0.0;
            boolean scored = false;
            for(Map.Entry<String, PostingList> p : postingLists.entrySet())
            {
                p.getValue().startIteration();
                p.getValue().skipTo(doc);
                Posting posting = p.getValue().getCurrentPosting();
                if(posting != null && posting.getDocID() == doc) {
                    double dL = index.getDocLengthMap().get(doc);
                    double avDL =  index.getAverageDocLength();
                    double K = k1 * ((1 - b) + (b * (dL/avDL)));
                    int N = index.getDocCount();
                    int nI = p.getValue().getDocumentCount();
                    int fI = posting.getTermFrequency();
                    int qfI = queryTermCounts.get(p.getKey());
                    double first = ((rI + 0.5) / (R - rI + 0.5))/((nI - rI + 0.5)/(N - nI - R + rI + 0.5));
                    double second = ((k1 + 1) * fI)/(K + fI);
                    double third = ((k2 + 1) * qfI)/(k2 + qfI);
                    score += Math.log(first) * second * third;
                    scored = true;
                }
            }
            if(scored) {
                pq.offer(new AbstractMap.SimpleEntry<>(doc, score));
                if (pq.size() > k) {
                    pq.poll();
                }
            }
        }
        List<Map.Entry<Integer, Double>> result = new ArrayList<>();
        while(!pq.isEmpty()){
            result.add(pq.poll());
        }
        result.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        return result;
    }
}
