package retrieval;

import index.InvertedIndex;
import index.Posting;
import index.PostingList;

import java.util.*;

public class VectorSpaceModel implements RetrievalModel {
    @Override
    public List<Map.Entry<Integer, Double>> retrieveQuery(InvertedIndex index, String[] queryTerms, int k, boolean compress) {
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        index.getQueryPostings(compress, queryTerms);
        Map<String, PostingList> postingLists = new HashMap<>();
        for(int i=0; i<queryTerms.length; i++) {
            postingLists.put(queryTerms[i], index.getPostings(queryTerms[i]));
        }
        Map<String, Integer> queryTermCounts = new HashMap<>();
        for(String query : queryTerms){
            queryTermCounts.putIfAbsent(query, 0);
            queryTermCounts.put(query, queryTermCounts.get(query)+1);
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
                int tf;
                if(posting != null && posting.getDocID() == doc) {
                        tf = posting.getTermFrequency();
                        double termLogTF = tf > 0 ? 1 + Math.log(posting.getTermFrequency()) : 0;
                        double queryLogTF = 1 + Math.log(queryTermCounts.get(p.getKey()));
                        double logIDF = Math.log((double)index.getDocCount()/(double)p.getValue().getDocumentCount());
                        score += termLogTF * logIDF * queryLogTF * logIDF;
                        scored = true;
                }
            }
            if(scored) {
                score /= index.getDocLengthMap().get(doc);
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
