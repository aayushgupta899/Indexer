package retrieval;

import index.InvertedIndex;
import index.Posting;
import index.PostingList;

import java.util.*;

public class QueryLikelihoodJMModel implements QLRetrievalModel {
    private double lambda;
    private long collectionSize;

    public  QueryLikelihoodJMModel(double lambda){
        this.lambda = lambda;
    }
    @Override
    public List<Map.Entry<Integer, Double>> retrieveQuery(InvertedIndex index, String[] queryTerms, int k, boolean compress) {
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        index.getQueryPostings(compress, queryTerms);
        PostingList[] postingLists = new PostingList[queryTerms.length];
        for(int i=0; i<queryTerms.length; i++) {
            postingLists[i] = index.getPostings(queryTerms[i]);
        }
        for(int doc=1; doc<=index.getDocCount(); doc++)
        {
            double score = 0.0;
            boolean scored = false;
            for(int i=0; i<postingLists.length; i++)
            {
                PostingList p = postingLists[i];
                p.startIteration();
                p.skipTo(doc);
                Posting posting = p.getCurrentPosting();
                int tf = 0;
                if(posting != null && posting.getDocID() == doc) {
                    tf = posting.getTermFrequency();
                    scored = true;
                }
                int documentLength = index.getDocLength(doc);
                int colTermFreq = index.getTermFrequency(queryTerms[i]);
                this.collectionSize = index.getCollectionSize();
                score += scoreOccurrence(tf, colTermFreq, documentLength);
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

    @Override
    public double scoreOccurrence(int termFrequency, int collectionTermFrequency, int documentLength) {
        double first = (1 - lambda) * termFrequency / documentLength;
        double second = lambda * collectionTermFrequency / this.collectionSize;
        return Math.log(first + second);
    }
}
