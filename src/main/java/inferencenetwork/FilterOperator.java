package inferencenetwork;

public abstract class FilterOperator implements QueryNode {

    protected QueryNode query = null;
    protected ProximityNode filter;

    public FilterOperator(ProximityNode proximityExp, QueryNode q){
        filter = proximityExp;
        query = q;
    }

    @Override
    public boolean hasMore() {
        return query.hasMore();
    }

    @Override
    public void skipTo(int docID) {
        filter.skipTo(docID);
        query.skipTo(docID);
    }
}
