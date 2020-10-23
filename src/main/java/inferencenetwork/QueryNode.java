package inferencenetwork;

public interface QueryNode {
    public Integer nextCandidate();
    public boolean hasMore();
    public void skipTo(int docID);
    public Double score(Integer docID);
}
