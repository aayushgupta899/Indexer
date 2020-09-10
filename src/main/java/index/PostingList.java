package index;

import java.util.ArrayList;
import java.util.List;

public class PostingList {

    private List<Posting> postings;

    public PostingList()
    {
        this.postings = new ArrayList<>();
    }

    public List<Posting> getPostings() {
        return postings;
    }

    public void setPostings(List<Posting> postings) {
        this.postings = postings;
    }

    public void add(int docID, int position)
    {
        boolean found = false;
        for(Posting posting : this.postings)
        {
            if(posting.getDocID() == docID)
            {
                posting.addPositionToPosting(position);
                found = true;
                break;
            }
        }
        if(!found)
        {
            Posting posting = new Posting();
            posting.setDocID(docID);
            List<Integer> positions = new ArrayList<>();
            positions.add(position);
            posting.setPositions(positions);
            this.add(posting);
        }
    }

    public void add(Posting posting)
    {
        this.postings.add(posting);
    }

    public int getDocumentCount()
    {
        return postings.size();
    }

    public Integer[] toIntegerArray()
    {
        List<Integer> result = new ArrayList<>();
        for(Posting p : postings)
        {
            result.addAll(p.toIntegerArray());
        }
        return result.toArray(new Integer[0]);
    }

    public void fromIntegerArray(int[] input)
    {
        int index = 0;
        while(index < input.length)
        {
            int docID = input[index++];
            int count = input[index++];
            for(int j=0; j<count; j++)
            {
                int position = input[index++];
                add(docID, position);
            }
        }
    }

    public int getTermFrequency()
    {
        int result = 0;
        for(Posting p : postings)
        {
            result += p.getTermFrequency();
        }
        return result;
    }


}
