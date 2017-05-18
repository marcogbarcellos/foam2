package foam.dao.index;

import foam.dao.Sink;
import foam.mlang.F;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

public class AbstractNode implements Node {
  protected Integer size;
  protected Integer level;
  protected AbstractNode left;
  protected AbstractNode right;
  
  @Override
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public AbstractNode getLeft() {
    return left;
  }

  public void setLeft(AbstractNode left) {
    this.left = left;
  }

  public AbstractNode getRight() {
    return right;
  }

  public void setRight(AbstractNode right) {
    this.right = right;
  }
  
  protected void updateSize() {
  }

  protected Node skew(boolean locked) {
    return this;
  }

  protected Node split(boolean locked) {
    return this;
  }

  protected Node decreaseLevel(boolean locked) {
    return this;
  }

  protected Node removeNode(Object key, Comparator compare, boolean locked) {
    return this;
  }
  
  @Override
  public Node bulkLoad(ArrayList<IndexNode> items, Integer start, Integer end, F keyExtractor) {
    return this;
  }  
  
  @Override
  public Node maybeClone(boolean locked) {
    return this;
  }

  @Override
  public Node get(Object key, Comparator compare) {
    return null;
  }

  @Override
  public ArrayList<Node> getAll(Object key, Comparator compare, ArrayList returnList) {
    return null;
  }

  @Override
  public Node putKeyValue(Object key, IndexNode value, Comparator compare, boolean locked) {
    return this;
  }

  @Override
  public Node removeKeyValue(Object key, IndexNode value, Comparator compare, boolean locked, Node nullNode) {
    return this;
  }

  @Override
  public Node gt(Object key, Comparator compare) {
    return this;
  }

  @Override
  public Node gte(Object key, Comparator compare) {
    return this;
  }

  @Override
  public Node lt(Object key, Comparator compare) {
    return this;
  }

  @Override
  public Node lte(Object key, Comparator compare) {
    return this;
  }
  
  @Override
  public void select(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
  }

  @Override
  public void selectReverse(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
  }  
  
}
