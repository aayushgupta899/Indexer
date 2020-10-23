package inferencenetwork.proximitynodes.windownodes.unorderedwindownodes;

import index.Index;
import inferencenetwork.ProximityNode;
import inferencenetwork.proximitynodes.windownodes.UnorderedWindowNode;
import retrieval.QLRetrievalModel;

import java.util.ArrayList;

public class BandNode extends UnorderedWindowNode {
    public BandNode(ArrayList<ProximityNode> termNodes, Index ind, QLRetrievalModel mod) {
        super(0, termNodes, ind, mod);
    }
}
