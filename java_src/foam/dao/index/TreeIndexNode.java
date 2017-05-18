package foam.dao.index;

import foam.core.ClassInfo;
import foam.core.FObject;
import foam.core.PropertyInfo;
import foam.core.X;
import foam.dao.DAO;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

public class TreeIndexNode extends AbstractIndexNode {
  private Node root;
  private Integer selectCount;

  public TreeIndexNode(PropertyInfo property) {
    this.index = new TreeIndex(property);
    this.root = new NullNode((AbstractIndex)this.index);
    this.selectCount = 0;
  }
  
  @Override
  public void put(FObject obj) {
    this.root = this.root.putKeyValue(
      ((TreeIndex)this.index).getProp().f(obj),
      (IndexNode)obj, 
      ((TreeIndex)this.index).getProp(), 
      this.selectCount > 0);
  }
  
  @Override
  public FObject get(FObject key) {
    return (FObject) this.root.get(key, ((TreeIndex)this.index).getProp());
  }
  
//  function remove(value) {
//      this.root = this.root.removeKeyValue(
//          this.index.prop.f(value),
//          value,
//          this.index.compare,
//          this.selectCount > 0,
//          this.index.nullNode);
//    },

  @Override
  public void remove(FObject obj) {
    this.root = this.root.removeKeyValue(
      ((TreeIndex)this.index).getProp().f(obj),
      (IndexNode)obj, 
      ((TreeIndex)this.index).getProp(), 
      this.selectCount > 0,
      root);
  }

  @Override
  public Plan plan(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate, FObject root) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Integer size() {
    return this.root.getSize();
  }

  @Override
  public void bulkLoad(DAO dao) {
    //TODO
  }

  @Override
  public void bulkLoad(ArrayList<IndexNode> items) {
    if ( ((TreeIndex)this.index).getTail() instanceof ValueIndex) {
      this.root = this.root.bulkLoad(items, 0, items.size()-1, ((TreeIndex)this.index).getProp());
    } else {
      for ( int i = 0 ; i < items.size() ; i++ ) {
        this.put(items.get(i));
      }
    }
  }

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

  
}
