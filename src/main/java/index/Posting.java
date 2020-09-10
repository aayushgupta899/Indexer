package index;

import java.util.ArrayList;
import java.util.List;

public class Posting {

    private int docID;
    private List<Integer> positions;

    public Posting()
    {
        this.positions = new ArrayList<>();
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

    public void addPositionToPosting(int position)
    {
        this.positions.add(position);
    }

    public int getTermFrequency()
    {
        return this.positions.size();
    }

    public Integer[] getPositionsArray()
    {
        return this.positions.toArray(new Integer[0]);
    }

    public ArrayList<Integer> toIntegerArray()
    {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(this.docID);
        result.add(this.positions.size());
        result.addAll(this.positions);
        return result;
    }
}
