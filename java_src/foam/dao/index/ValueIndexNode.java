package foam.dao.index;

import foam.core.ClassInfo;
import foam.core.FObject;
import foam.core.X;
import foam.dao.DAO;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

/**
  An Index which holds only a single value. This class also functions as its
  own execution Plan, since it only has to return the single value.
**/
public class ValueIndexNode extends AbstractIndexNode implements Plan {
    private Integer cost;
    private FObject value;  
    
    public ValueIndexNode(Integer cost) {
        super();
        this.cost = cost;
        this.index = new ValueIndex(this.getClass());
    }
    

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
    
    @Override
    public void put(FObject obj) {
        this.value = obj;
    }

    @Override
    public FObject get(FObject key) {
        return this.value;
    }

    @Override
    public void remove(FObject obj) {
        this.value = null;
    }

    @Override
    public Plan plan(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate, FObject root) {
        this.cost = 1;
        return this;
    }

    @Override
    public Integer size() {
        return (this.value != null) ? 1 : 0;
    }

    @Override
    public void bulkLoad(DAO dao) {
        // TODO
    }

    @Override
    public void execute(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
        sink.put(null, this.value);
    }
    
    //The method select was removed from IndexNode Interface(if you compare to the javascript interface
    // you'll see it's there)

  @Override
  public ClassInfo getClassInfo() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public X getX() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setX(X x) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int compareTo(Object o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void bulkLoad(ArrayList<IndexNode> items) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
