package inferencenetwork.filteroperators;

import inferencenetwork.FilterOperator;
import inferencenetwork.ProximityNode;
import inferencenetwork.QueryNode;

public class FilterReject extends FilterOperator {
    public FilterReject(ProximityNode proximityExp, QueryNode q) {
        super(proximityExp, q);
    }

    @Override
    public Integer nextCandidate() {
        return query.nextCandidate();
    }

    @Override
    public Double score(Integer docID) {
        if(!docID.equals(filter.nextCandidate())){
            return query.score(docID);
        }
        else {
            return null;
        }
    }
}
