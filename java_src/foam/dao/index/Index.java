package foam.dao.index;

import foam.core.FObject;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.Comparator;

public interface Index {
    
    public Double estimate(Integer size, Sink sink, Integer skip, 
                             Integer limit, Comparator order, Predicate predicate);
    public String toPrettyString(String indent);
    public FObject createNode() throws Exception;
}
