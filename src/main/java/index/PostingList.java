package index;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Posting List
 */
public class PostingList {

    private List<Posting> postings;
    private int postingsIndex;

    public PostingList()
    {
        this.postings = new ArrayList<>();
        this.postingsIndex = -1;
    }

    /**
     * reset the list pointer to the first element
     */
    public void startIteration () {
        postingsIndex = 0;
    }
    /**
     * are there any more
     * @return true if there are remaining elements in the list
     */
    public boolean hasMore() {
        return (postingsIndex >= 0 && postingsIndex < postings.size());
    }
    /**
     * skip to or past the specified document id
     * @param docid the id to skip to
     *
     */
    public void skipTo (int docid) {
        while (postingsIndex < postings.size() &&
                getCurrentPosting().getDocID() < docid) {
            postingsIndex++;
        }
    }
    /**
     *
     * @return the current posting in the list or null if the list is empty
     * or consumed
     */
    public Posting getCurrentPosting() {
        Posting retval = null;
        try {
            retval = postings.get(postingsIndex);
        } catch (IndexOutOfBoundsException ex) {
            // ignore
        }
        return retval;
    }

    public List<Posting> getPostings() {
        return postings;
    }

    public void setPostings(List<Posting> postings) {
        this.postings = postings;
    }

    /**
     * Adds an entry into the Posting List
     * @param docID The docID
     * @param position The position of the term in the doc
     */
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

    /**
     * Adds an entry into the Posting list
     * @param posting The Posting object to add
     */
    public void add(Posting posting)
    {
        this.postings.add(posting);
    }

    /**
     * Gets the document count
     * @return The document count
     */
    public int getDocumentCount()
    {
        return postings.size();
    }

    /**
     * Converts the Posting List into an Integer array
     * @return The Posting List Integer Array
     */
    public Integer[] toIntegerArray()
    {
        List<Integer> result = new ArrayList<>();
        for(Posting p : postings)
        {
            result.addAll(p.toIntegerArray());
        }
        return result.toArray(new Integer[0]);
    }

    /**
     * Gets a Posting List object from an Integer Array
     * @param input Integer array containing the Posting List
     */
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

    /**
     * Gets the term frequency
     * @return The term frequency
     */
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
