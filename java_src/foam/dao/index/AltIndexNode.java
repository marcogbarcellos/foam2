package foam.dao.index;

import foam.core.ClassInfo;
import foam.core.FObject;
import foam.core.X;
import foam.dao.DAO;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

public class AltIndexNode extends AbstractIndexNode{
    
    private ArrayList<IndexNode> delegates;
    
    public AltIndexNode() {
        super();
        this.index = new AltIndex(this.getClass());
        delegates = new ArrayList();
        
    }
    
    public void addIndex(Index index, DAO dao) throws Exception {
        IndexNode newIndex = (IndexNode) this.index.createNode();
        newIndex.bulkLoad(dao);
        this.delegates.add(newIndex);
    }
    
    @Override
    public void put(FObject obj) {
        for ( int i = 0; i < this.delegates.size(); i++ ) {
            this.delegates.get(i).put(obj);
        }
    }

    @Override
    public FObject get(FObject key) {
        return this.delegates.get(0).get(key);
    }

    @Override
    public void remove(FObject obj) {
        for ( int i = 0; i < this.delegates.size(); i++ ) {
            this.delegates.get(i).remove(obj);
        }
    }

    @Override
    public Plan plan(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate, FObject root) {
        Plan bestPlan = null;
        for ( int i = 1; i < this.delegates.size(); i++ ) {
            AbstractPlan p = (AbstractPlan) this.delegates.get(i)
                             .plan(sink, skip, limit, order, predicate, root);
            if (p.cost <= AltIndex.GOOD_ENOUGH_PLAN) {
               bestPlan = p;
               break;
            }
        }
        return bestPlan;
    }
    
    @Override
    public Integer size() {
        return this.delegates.size();
    }

    @Override
    public void bulkLoad(DAO dao) {
        for ( int i = 0; i < this.delegates.size(); i++ ) {
            this.delegates.get(i).bulkLoad(dao);
        }
    }
    
    // method select removed...

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
