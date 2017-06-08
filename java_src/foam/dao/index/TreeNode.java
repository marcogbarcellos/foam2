/**
 * @license
 * Copyright 2017 The FOAM Authors. All Rights Reserved.
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package foam.dao.index;

import foam.core.FObject;
import foam.core.PropertyInfo;


public class TreeNode {
  
  protected Index tail;
  protected Object key;
  protected Object value;
  protected long size;
  protected long level;
  protected TreeNode left;
  protected TreeNode right;
  
  protected static TreeNode nullNode;
  
  public TreeNode(Index tail, Object key, Object value) {
    this.tail = tail;
    this.key = key;
    
    this.value = value;
  }
  
  public TreeNode(Index tail, Object key, Object value, long size, long level,
                  TreeNode left, TreeNode right) {
    this.tail = tail;
    this.key = key;
    this.value = value;
    this.size = size;
    this.level = level;
    this.left = left;
    this.right = right;
  }
  public TreeNode cloneNode() {
    return new TreeNode(this.tail, this.key, this.value, this.size, 
                               this.level, this.left, this.right);
    
  } 
  private TreeNode maybeClone(TreeNode s) {
    if ( s != null ) {
      return s.cloneNode();
    }
    return s;
  }
  public static TreeNode getNullNode() {
    if ( nullNode != null ) {
      return nullNode;
    }
    nullNode = new TreeNode(null, null, null);
    nullNode.level = nullNode.size = 0;
    nullNode.left = nullNode.right = null;
    return nullNode;
  }
  
  public Object bulkLoad(PropertyInfo prop, int start, int end, FObject... a) {
    if( end < start ) {
      return null;
    }
    int m = start + (int)Math.floor((end-start+1)/2);
    TreeNode tree = this.putKeyValue(this, prop, prop.f(a[m]), a[m]);
    tree.left = (TreeNode)this.bulkLoad(prop, start, m-1, a);
    tree.right = (TreeNode)this.bulkLoad(prop, m+1, end, a);
    tree.size = this.size(tree.left) + this.size(tree.right);
    return tree;
  }
  
  public TreeNode putKeyValue(TreeNode state, PropertyInfo prop, Object key,
    FObject value) {
    if ( state == null || state.equals(TreeNode.getNullNode()) ) {
      return new TreeNode(this.tail, key, value, 1, 1, null, null);
    }
    state = maybeClone(state);
    int r = prop.compare(state.key, key);
    
    if ( r == 0 ) {
      state.size -= this.tail.size(state.value);
      state.value = this.tail.put(state.value, value);
      state.size += this.tail.size(state.value);
    } else {
      if ( r > 0 ) {
        if ( state.left != null ) {
          state.size -= state.left.size;
        }
        state.left = this.putKeyValue(state.left, prop, key, value);
        state.size += state.left.size;
      } else {
        if ( state.right != null ) {
          state.size -= state.right.size;
        }
        state.right = this.putKeyValue(state.right, prop, key, value);
        state.size += state.right.size;
      }
    }
    return split(skew(state));
  }
  
  public TreeNode skew(TreeNode node) {
    if ( node != null && node.left != null && node.left.level == node.level ) {
      // Swap the pointers of horizontal left links.
      TreeNode l = maybeClone(node.left);
      
      node.left = l.right;
      l.right = node;
      
      updateSize(node);
      updateSize(l);
      return l;
    }
    return node;
  }
  
  public TreeNode split(TreeNode node) {
    if ( node != null && node.right != null && node.right.right != null &&
        node.level == node.right.right.level ) {
      // Swap the pointers of horizontal left links.
      TreeNode r = maybeClone(node.right);
      
      node.right = r.left;
      r.left = node;
      r.level++;
      
      updateSize(node);
      updateSize(r);
      return r;
    }
    return node;
  }
  
  public TreeNode removeKeyValue(TreeNode state, PropertyInfo prop, Object key,
    FObject value) {
    if ( state == null ) {
      return state;
    }
    
    state = maybeClone(state);
    long compareValue = prop.compare(state.key, key);
    
    if ( compareValue == 0 ) {
      state.size -= this.tail.size(state.value);
      state.value = this.tail.remove(state.value, value);
      
      if ( state.value != null ) {
        state.size += this.tail.size(state.value);
        return state;
      }
      
      if ( state.left == null && state.right == null ) {
        return null;
      }
      boolean isLeft = ( state.left != null );
      TreeNode subs = isLeft ? predecessor(state) : successor(state);
      state.key = subs.key;
      state.value = subs.value;
      if( isLeft ) {
        state.left = removeNode(state.left, subs.key, prop);
      } else {
        state.right = removeNode(state.right, subs.key, prop);
      }
    } else {
      if ( compareValue > 0 ) {
        state.size -= size(state.left);
        state.left = removeKeyValue(state.left, prop, key, value);
        state.size += size(state.left);
      } else {
        state.size -= size(state.right);
        state.right = removeKeyValue(state.right, prop, key, value);
        state.size += size(state.right);
      }
    }
    // Rebalance the tree. Decrease the level of all nodes in this level if
    // necessary, and then skew and split all nodes in the new level.
    state = skew(decreaseLevel(state));
    if ( state.right != null ) {
      state.right = skew(maybeClone(state.right));
      if ( state.right.right != null ) {
        state.right.right = skew(maybeClone(state.right.right));
      }
    }
    state = split(state);
    state.right = split(maybeClone(state.right));
    
    return state;
  }
  
  private void removeSideKeyValueNode(TreeNode parent, TreeNode side, PropertyInfo prop,
    Object key, FObject value) {
    parent.size -= size(side);
    side = removeKeyValue(side, prop, key, value);
    parent.size += size(side);
  }
  
  private TreeNode removeNode(TreeNode state, Object key, PropertyInfo prop) {
    if ( state == null ) {
      return state;
    }
    state  = maybeClone(state);
    long compareValue = prop.compare(state.key, key);
    
    if ( compareValue == 0 ) {
      return state.left != null ? state.left : state.right;
    }
    if ( compareValue > 0 ) {
      removeSideNode(state, state.left, prop, key);
    } else {
      removeSideNode(state, state.right, prop, key);
    }
    return state;
  }
  
  private void removeSideNode(TreeNode parent, TreeNode side, PropertyInfo prop,
    Object key) {
    parent.size -= size(side);
    side = removeNode(side, key, prop);
    parent.size += size(side);
  }
  
  private TreeNode predecessor(TreeNode node) {
    if ( node.left == null ) {
      return node;
    }
    node = node.left;
    while ( node.right != null ) {
      node = node.right;
    }
    return node;
  }
  
  private TreeNode successor(TreeNode node) {
    if ( node.right == null ) {
      return node;
    }
    node = node.right;
    while ( node.left != null ) {
      node = node.left;
    }
    return node;
  }
  
  private TreeNode decreaseLevel(TreeNode node) {
    long expectedLevel = 1 + Math.min(
      node.left != null ? node.left.level : 0 ,
      node.right != null ? node.right.level : 0);
    
    if ( expectedLevel < node.level ) {
      node.level = expectedLevel;
      if ( node.right != null && expectedLevel < node.right.level ) {
        node.right = maybeClone(node.right);
        node.right.level = expectedLevel;
      }
    }
    return node;
  }
  
  private void updateSize(TreeNode node) {
    node.size = size(node.left) + size(node.right) + this.tail.size(node.value);
  }
  
  private long size (TreeNode node) {
    if ( node != null ) {
      return node.size;
    }
    return 0;
  }
  
  public Object get(TreeNode s, Object key, PropertyInfo prop) {
    if ( s == null ) {
      return s;
    }
    int r = prop.compare(s.key, key);
    if ( r == 0 ) {
      return s.value;
    } else if ( r > 0 ) {
      return get(s.left, key, prop);
    } else {
      return get(s.right, key, prop);
    }
  }
  public TreeNode gt(TreeNode s, Object key, PropertyInfo prop) {
    if ( s == null ) {
      return s;
    }
    int r = prop.compare(s.key, key);
    if ( r < 0 ) {
      TreeNode l = gt(s.left, key, prop);
      long newSize = size(s) - size(s.left) + size(l);
      return new TreeNode(s.tail, s.key, s.value, newSize, 
        s.level, l, s.right);
    } 
    if ( r > 0 ) {
      return gt(s.right, key, prop);
    }
    
    return s.right;
  }
  
  public TreeNode gte(TreeNode s, Object key, PropertyInfo prop) {
    if ( s == null ) {
      return s;
    }
    int r = prop.compare(s.key, key);
    if ( r < 0 ) {
      TreeNode l = gte(s.left, key, prop);
      long newSize = size(s) - size(s.left) + size(l);
      return new TreeNode(s.tail, s.key, s.value, newSize, 
        s.level, l, s.right);
    } 
    if ( r > 0 ) {
      return gte(s.right, key, prop);
    }
    
    return new TreeNode(s.tail, s.key, s.value, size(s) - size(s.left), 
      s.level, null, s.right);
  }
  public TreeNode lt(TreeNode s, Object key, PropertyInfo prop) {
    if ( s == null ) {
      return s;
    }
    int r = prop.compare(s.key, key);
    if ( r > 0 ) {
      TreeNode right = lt(s.right, key, prop);
      long newSize = size(s) - size(s.right) + size(right);
      return new TreeNode(s.tail, s.key, s.value, newSize, 
        s.level, s.left, right);
    } 
    if ( r < 0 ) {
      return lt(s.left, key, prop);
    }
    
    return  s.left;
  }
  public TreeNode lte(TreeNode s, Object key, PropertyInfo prop) {
    if ( s == null ) {
      return s;
    }
    int r = prop.compare(s.key, key);
    if ( r > 0 ) {
      TreeNode right = lte(s.right, key, prop);
      long newSize = size(s) - size(s.right) + size(right);
      return new TreeNode(s.tail, s.key, s.value, newSize, 
        s.level, s.left, right);
    } 
    if ( r < 0 ) {
      return lte(s.left, key, prop);
    }
    
    return new TreeNode(s.tail, s.key, s.value, size(s) - size(s.right), 
      s.level, s.left, null);
  }
  
}
