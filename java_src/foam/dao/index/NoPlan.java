package foam.dao.index;

import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.Comparator;

/** Plan indicating that an index has no plan for executing a query. **/
public class NoPlan extends AbstractPlan{
    
    protected static NoPlan noPlan;
    
    private NoPlan(Integer n) {
        this.cost = n;
    }
    
    public static NoPlan getInstance() {
        if (noPlan == null) {
           noPlan = new NoPlan(Integer.MAX_VALUE); 
        }
        return noPlan;
    }
    @Override
    public void execute(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
        //Do nothing.
    }

    @Override
    public String toString() {
        return "no-plan";
    }
    
}
