package foam.dao.index;

import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import foam.mlang.sink.Count;
import java.util.Comparator;

public class CountPlan extends AbstractPlan{
   
    private Integer count;

    public CountPlan(Integer count) {
        this.count = count;
    }
    
    @Override
    public void execute(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
        ((Count) sink).setValue(this.count);
    }
    
    @Override
    public String toString() {
        return "short-circuit-count(" + this.count + ")";
    }
}
