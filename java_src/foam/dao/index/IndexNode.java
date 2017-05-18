package foam.dao.index;

import foam.core.FObject;
import foam.dao.DAO;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

public interface IndexNode extends FObject {
    //The method select was removed(if you compare to the javascript interface
    // you'll see it's there)
    public void put(FObject obj);
    public FObject get(FObject key);
    public void remove(FObject obj);
    public Plan plan(Sink sink, Integer skip, Integer limit, Comparator order, 
                     Predicate predicate, FObject root);
    public Integer size();
    public void bulkLoad(DAO dao);
    public void bulkLoad(ArrayList<IndexNode> items);
}