package foam.dao.index;

import foam.core.FObject;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.Comparator;

public abstract class AbstractIndex implements Index {
    protected Class nodeClass;

    public AbstractIndex(Class nodeClass) {
        this.nodeClass = nodeClass;
    }
    
    @Override
    public FObject createNode() throws Exception {
        return (FObject) nodeClass.getDeclaredConstructor(nodeClass)
                         .newInstance();
    }
    
    @Override
    public String toPrettyString(String indent) {
        //TODO
        return indent;
    }

    @Override
    public Double estimate(Integer size, Sink sink, Integer skip, 
                            Integer limit, Comparator order, 
                            Predicate predicate) {
        return 1.0*size*size;
    }
    
}
