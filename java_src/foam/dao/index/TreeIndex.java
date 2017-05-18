package foam.dao.index;

import foam.core.PropertyInfo;
import foam.dao.Sink;
import foam.mlang.predicate.Binary;
import foam.mlang.predicate.Predicate;
import java.util.Comparator;

public class TreeIndex extends AbstractIndex {
  private PropertyInfo prop;
  private Index tail;
  
  public TreeIndex(PropertyInfo prop) {
    super(TreeIndexNode.class);
    this.prop = prop;
    ;
    this.tail = new ValueIndex(nodeClass);
  }
  
  public TreeIndex(PropertyInfo prop, Index index) {
    super(TreeIndexNode.class);
    this.prop = prop;
    this.tail = index;
  }
  
  public PropertyInfo getProp() {
    return prop;
  }

  public Index getTail() {
    return tail;
  }
  
  public NullNode newNullNode() {
    return new NullNode((AbstractIndex)this.tail);
  }
  
  //TODO problem: On the javascript version the getArg returns a number as 
  //a way of comparison, on java it returns a boolean.
  private boolean isExprMatch(Binary predicate, PropertyInfo prop) {
    return false;
  }
  
  
  private Double subestimate(Integer size, Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
    Double nodeCount = Math.floor(size * 0.25);
    return ( Math.log(nodeCount)/ Math.log(2.0) ) +
      this.tail.estimate(size, sink, skip, limit, order, predicate);
  }
  
  private Boolean isOrderSelectable(Comparator order) {
    // no ordering, no problem
    if(order == null) {
      return true;
    } 
    //FROM JAVASCRIPT -> Comparator.orderTail is void and not a type.
    // if this index can sort, it's up to our tail to sub-sort
//    if ( foam.util.equals(order.orderPrimaryProperty(), this.prop) ) {
//      // If the subestimate is less than sort cost (N*lg(N) for a dummy size of 1000)
//      return 9965 >
//        this.tail.estimate(1000, this.NullSink.create(), 0, 0, order.orderTail())
//    }

    // can't use select() with the given ordering
    return false;
  }
  
  //  private Expr isExprMatch(Predicate predicate, PropertyInfo prop,)
  //  IS_EXPR_MATCH_FN: function isExprMatch(predicate, prop, model) {

  
  @Override
  public Double estimate(Integer size, Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
    // small sizes don't matter
    if(size <= 16) {
      return Math.log(size)/Math.log(2);
    }
    // if only estimating by ordering, just check if we can scan it
      //  otherwise return the sort cost.
      // NOTE: This is conceptually the right thing to do, but also helps
      //   speed up isOrderSelectable() calls on this:
      //   a.isOrderSelectable(o) -> b.estimate(..o) -> b.isOrderSelectable(o) ...
      //   Which makes it efficient but removes the need for Index to
      //   have an isOrderSelectable() method forwarding directly.
    if(order != null && 
      !(predicate != null || skip != null || limit != null)) {
      if(isOrderSelectable(order)) {
        return 1.0 * size;
      } else {
        return size * (Math.log(size) / Math.log(2));
      }
    }
    //while we don't have IS_EXPR_MATCH_FN
    Double cost  = 1.0 * size;
    return cost;
  }
  
  

  
}
