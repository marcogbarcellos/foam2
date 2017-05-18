package foam.dao.index;

import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

public class AltIndex extends AbstractIndex {
    
    public final static Integer GOOD_ENOUGH_PLAN = 10; 
    private ArrayList<Index> delegates;
    
    public AltIndex(Class nodeClass) {
        super(nodeClass);
        delegates = new ArrayList();
    }
    
    @Override
    public Double estimate(Integer size, Sink sink, Integer skip, 
                            Integer limit, Comparator order, 
                            Predicate predicate) {
        Double cost = Double.MAX_VALUE;
        for ( int i = 0; i < this.delegates.size(); i++ ) {
            cost = Math.min(cost, this.delegates.get(i).estimate(
                                size, sink, skip, limit, order, predicate)
                           );
        }
        return cost;
    }

    @Override
    public String toPrettyString(String indent) {
        String str = "";
        for ( int i = 0; i < this.delegates.size(); i++ ) {
            str += this.delegates.get(i).toPrettyString(indent)+" ";
        }
        return str;
    }

    public String toString(String indent) {
        String str = "Alt([";
        for ( int i = 0; i < this.delegates.size(); i++ ) {
            str += this.delegates.get(i).toString()+", ";
        }
        str += "])";
        return str;
    }

}
