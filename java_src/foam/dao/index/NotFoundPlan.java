package foam.dao.index;

import foam.core.ClassInfo;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.Comparator;

/** Plan indicating that there are no matching records. **/
public class NotFoundPlan extends AbstractPlan {
    
    protected static NotFoundPlan plan;
    
    private NotFoundPlan(Integer n) {
        this.cost = n;
    }
    
    public static NotFoundPlan getInstance() {
        if (plan == null) {
           plan = new NotFoundPlan(0); 
        }
        return plan;
    }
    
    @Override
    public void execute(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
        //Do nothing.
    }

    @Override
    public String toString() {
        return "no-match(cost=0)";
    }
    
}
