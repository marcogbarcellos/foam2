package foam.dao.index;

import foam.core.ClassInfo;
import foam.core.ClassInfoImpl;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.Comparator;

public class ValueIndex extends AbstractIndex {
    
    public ValueIndex(Class nodeClass) {
        super(nodeClass);
    }
    
    @Override
    public Double estimate(Integer size, Sink sink, Integer skip, 
                            Integer limit, Comparator order, 
                            Predicate predicate) {
        return 1.0;
    }

    @Override
    public String toPrettyString(String indent) {
        return "";
    }

}
