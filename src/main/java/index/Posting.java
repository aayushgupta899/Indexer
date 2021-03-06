package index;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the posting for a particular document
 * Consists of docID and a list of positions
 */
public class Posting {

    private int docID;
    private List<Integer> positions;

    public Posting()
    {
        this.positions = new ArrayList<>();
    }

    public Posting(Integer docID, Integer position) {
        this.positions = new ArrayList<>();
        this.positions.add(position);
        this.docID = docID;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }


    /**
     * Adds a position to the positions array
     * @param position The position to add
     */
    public void addPositionToPosting(int position)
    {
        this.positions.add(position);
    }

    /**
     * Gets the term frequency
     * @return The term frequency
     */
    public int getTermFrequency()
    {
        return this.positions.size();
    }

    /**
     * Returns the positions list in the form of an Integer array
     * @return Positions Integer array
     */
    public Integer[] getPositionsArray()
    {
        return this.positions.toArray(new Integer[0]);
    }

    /**
     * Converts the Posting to an integer ArrayList in the following format:
     * docID, positions_size, position_1, position_2...
     * @return The Posting converted to an Integer ArrayList
     */
    public ArrayList<Integer> toIntegerArray()
    {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(this.docID);
        result.add(this.positions.size());
        result.addAll(this.positions);
        return result;
    }
}
