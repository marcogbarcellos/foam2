package foam.dao.index;

import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.Comparator;

public interface Plan {
    //removing promise and state params(compared to javascript interface)
    public void execute(Sink sink, Integer skip, Integer limit, Comparator order, 
                        Predicate predicate);
}
