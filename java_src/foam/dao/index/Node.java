package foam.dao.index;

import foam.dao.Sink;
import foam.mlang.F;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

public interface Node {

  public Node maybeClone(boolean locked);
  
  public Integer getSize();
  
  public Node get(Object key, Comparator compare);
  
  public ArrayList<Node> getAll(Object key, Comparator compare,
    ArrayList returnList);

  public Node putKeyValue(Object key, IndexNode value, Comparator compare,
    boolean locked);

  public Node removeKeyValue(Object key, IndexNode value, Comparator compare,
    boolean locked, Node nullNode);
  
  
  public Node gt(Object key, Comparator compare);
  //maybe use nullNode here..

  public Node gte(Object key, Comparator compare);

  public Node lt(Object key, Comparator compare);
  //maybe use nullNode here..

  public Node lte(Object key, Comparator compare);

  public void select(Sink sink, Integer skip, Integer limit, Comparator order,
    Predicate predicate);

  public void selectReverse(Sink sink, Integer skip, Integer limit, Comparator order,
    Predicate predicate);
  
  public Node bulkLoad(ArrayList<IndexNode> items, Integer start, Integer end,
    F keyExtractor);
}
