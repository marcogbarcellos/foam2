package foam.dao.index;

import foam.core.FObject;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

public class TreeNode extends AbstractNode {

  private Object key;
  private IndexNode value;

  public TreeNode(Object key, IndexNode value, Integer size, Integer level, AbstractNode left, AbstractNode right) {
    this.key = key;
    this.value = value;
    this.size = size;
    this.level = level;
    this.left = left;
    this.right = right;
  }

  @Override
  public Node maybeClone(boolean locked) {
    try {
      return locked ? (TreeNode) this.clone() : this;
    } catch (CloneNotSupportedException ex) {
      return this;
    }
  }

  @Override
  protected void updateSize() {
    this.size = this.left.size + this.right.size + this.value.size();
  }

  @Override
  protected Node skew(boolean locked) {
    if (this.left.level == this.level) {
      TreeNode l = (TreeNode) this.left.maybeClone(locked);
      this.left = l.right;
      l.right = this;

      this.updateSize();
      l.updateSize();

      return l;
    } else {
      return this;
    }
  }

  @Override
  protected Node split(boolean locked) {
    if (this.right != null && this.right.right != null
      && this.level == this.right.right.level) {
      TreeNode r = (TreeNode) this.right.maybeClone(locked);
      this.right = r.left;
      r.left = this;
      r.level++;

      this.updateSize();
      r.updateSize();

      return r;
    } else {
      return this;
    }
  }

  private Node predecessor() {
    if (this.left == null) {
      return this;
    }
    TreeNode current = (TreeNode) this.left;
    while (current.right != null) {
      current = (TreeNode) current.right;
    }
    return current;
  }

  private Node successor() {
    if (this.right == null) {
      return this;
    }
    TreeNode current = (TreeNode) this.right;
    while (current.left != null) {
      current = (TreeNode) current.left;
    }
    return current;
  }

  @Override
  protected Node decreaseLevel(boolean locked) {
    Integer expectedLevel = Math.min(
      this.left != null ? this.left.level : 0,
      this.right != null ? this.right.level : 0) + 1;
    if (expectedLevel < this.level) {
      this.level = expectedLevel;
      if (this.right != null && expectedLevel < this.right.level) {
        this.right = (TreeNode) this.right.maybeClone(locked);
        this.right.level = expectedLevel;
      }
    }
    return this;
  }

  @Override
  public Node get(Object key, Comparator compare) {
    Integer comparingValue = compare.compare(this.key, key);
    if (comparingValue == 0) {
      return this;
    } else if (comparingValue > 0) {
      return this.left.get(key, compare);
    } else {
      return this.right.get(key, compare);
    }

  }

  @Override
  public ArrayList<Node> getAll(Object key, Comparator compare, ArrayList returnList) {
    if (this != null) {
      Integer comparingValue = compare.compare(this.key, key);
      if (comparingValue == 0) {
        returnList.add(this);
      }
      this.left.getAll(key, compare, returnList);
      this.right.getAll(key, compare, returnList);
    }
    return returnList;
  }

  @Override
  public Node putKeyValue(Object key, IndexNode value, Comparator compare,
    boolean locked) {
    TreeNode current = (TreeNode) this.maybeClone(locked);
    Integer comparingValue = compare.compare(current.key, key);
    if (comparingValue == 0) {
      current.size -= current.value.size();
      current.value.put((FObject) value);
      current.size += current.value.size();
    } else {
      if (comparingValue > 0 && current.left != null) {
        current.size -= current.left.size;
        current.left = (TreeNode) current.left.putKeyValue(key, value, compare, locked);
        current.size += current.left.size;
      } else if (current.right != null) {
        current.size -= current.right.size;
        current.right = (TreeNode) current.right.putKeyValue(key, value, compare, locked);
        current.size += current.right.size;
      }

    }
    current = (TreeNode) skew(locked);
    current = (TreeNode) split(locked);
    return current;
  }

  @Override
  public Node removeKeyValue(Object key, IndexNode value, Comparator compare, boolean locked, Node nullNode) {
    TreeNode current = (TreeNode) this.maybeClone(locked);
    Integer comparingValue = compare.compare(current.key, key);
    if (comparingValue == 0) {
      current.size -= current.value.size();
      current.value.remove((FObject) value);
      // If the sub-Index still has values, then don't
      // delete this node.
      if (current.value != null) {
        current.size += current.value.size();
        return current;
      }

      // If we're a leaf, easy, otherwise reduce to leaf case.
      if (current.left != null && current.right != null) {
        return nullNode;
      }

      if (current.left != null) {
        TreeNode l = (TreeNode) current.predecessor();
        current.key = l.key;
        current.value = l.value;
        current.left = (TreeNode) current.left.removeNode(l.key, compare, locked);
      } else {
        TreeNode r = (TreeNode) current.successor();
        current.key = r.key;
        current.value = r.value;
        current.right = (TreeNode) current.right.removeNode(r.key, compare, locked);
      }
    } else {
      if (comparingValue > 0) {
        current.size -= current.left.size;
        current.left = (TreeNode) current.left.removeKeyValue(key, value, compare, locked, nullNode);
        current.size += current.left.size;
      } else {
        current.size -= current.right.size;
        current.right = (TreeNode) current.right.removeKeyValue(key, value, compare, locked, nullNode);
        current.size += current.right.size;
      }
    }
    // Rebalance the tree. Decrease the level of all nodes in this level if
    // necessary, and then skew and split all nodes in the new level.

    current = (TreeNode) current.decreaseLevel(locked);
    current = (TreeNode) current.skew(locked);
    if (current.right != null) {
      TreeNode temp = (TreeNode) current.right.maybeClone(locked);
      temp = (TreeNode) temp.skew(locked);
      current.right = temp;
      if (current.right.right != null) {
        TreeNode tempRight = (TreeNode) current.right.right.maybeClone(locked);
        tempRight = (TreeNode) tempRight.skew(locked);
        current.right.right = temp;
      }
    }
    current = (TreeNode) current.split(locked);
    TreeNode temp = (TreeNode) current.right.maybeClone(locked);
    temp = (TreeNode) temp.split(locked);
    current.right = temp;
    return current;
  }

  @Override
  protected Node removeNode(Object key, Comparator compare, boolean locked) {
    TreeNode current = (TreeNode) this.maybeClone(locked);
    Integer comparingValue = compare.compare(current.key, key);
    if (comparingValue == 0) {
      if (current.left != null) {
        return current.left;
      } else {
        return current.right;
      }
    } else if (comparingValue > 0) {
      current.size -= current.left.size;
      current.left = (TreeNode) current.left.removeNode(key, compare, locked);
      current.size += current.left.size;
    } else {
      current.size -= current.right.size;
      current.right = (TreeNode) current.right.removeNode(key, compare, locked);
      current.size += current.right.size;
    }
    return current;
  }

  @Override
  public Node gt(Object key, Comparator compare) {
    Integer comparingValue = compare.compare(this.key, key);
    if (comparingValue < 0) {
      TreeNode left = (TreeNode) this.left.gt(key, compare);
      TreeNode copy = (TreeNode) this.maybeClone(false);
      copy.size = this.size - this.left.size + left.size;
      copy.left = left;
      return copy;
    }
    if (comparingValue > 0) {
      return this.right.gt(key, compare);
    }
    return this.right;
  }

  //maybe use nullNode here..
  @Override
  public Node gte(Object key, Comparator compare) {
    Integer comparingValue = compare.compare(this.key, key);
    TreeNode copy = (TreeNode) this.maybeClone(false);
    if (comparingValue < 0) {
      TreeNode left = (TreeNode) this.left.gte(key, compare);
      copy.size = this.size - this.left.size + left.size;
      copy.left = left;
      return copy;
    }
    if (comparingValue > 0) {
      return this.right.gte(key, compare);
    }
    copy.size = this.size - this.left.size;
    copy.left = null;
    return copy;
  }

  @Override
  public Node lt(Object key, Comparator compare) {
    Integer comparingValue = compare.compare(this.key, key);
    if (comparingValue > 0) {
      TreeNode right = (TreeNode) this.right.lt(key, compare);
      TreeNode copy = (TreeNode) this.maybeClone(false);
      copy.size = this.size - this.right.size + right.size;
      copy.right = right;
      return copy;
    }
    if (comparingValue < 0) {
      return this.left.lt(key, compare);
    }
    return this.left;
  }

  //maybe use nullNode here..
  @Override
  public Node lte(Object key, Comparator compare) {
    Integer comparingValue = compare.compare(this.key, key);
    TreeNode copy = (TreeNode) this.maybeClone(false);
    if (comparingValue > 0) {
      TreeNode right = (TreeNode) this.right.lte(key, compare);
      copy.size = this.size - this.right.size + right.size;
      copy.right = right;
      return copy;
    }
    if (comparingValue < 0) {
      return this.left.lte(key, compare);
    }
    copy.size = this.size - this.right.size;
    copy.right = null;
    return copy;
  }

  @Override
  public void select(Sink sink, Integer skip, Integer limit, Comparator order,
    Predicate predicate) {
    if ((limit == null || limit <= 0)
      || (skip != null && skip >= this.size && predicate == null)) {
      return;
    }
    this.left.select(sink, skip, limit, order, predicate);
    //select not implemented on interface: this.value.select 
    this.right.select(sink, skip, limit, order, predicate);
  }

  @Override
  public void selectReverse(Sink sink, Integer skip, Integer limit, Comparator order,
    Predicate predicate) {
    if ((limit == null || limit <= 0)
      || (skip != null && skip >= this.size && predicate == null)) {
      return;
    }
    this.right.selectReverse(sink, skip, limit, order, predicate);
    //select not implemented on interface: this.value.select 
    this.left.selectReverse(sink, skip, limit, order, predicate);
  }
}
