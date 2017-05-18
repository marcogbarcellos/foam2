package foam.dao.index;

import foam.core.FObject;
import foam.mlang.F;
import java.util.ArrayList;
import java.util.Comparator;

public class NullNode extends AbstractNode {
    protected AbstractIndex tail;
    
    public NullNode(AbstractIndex tail) {
      this.tail = tail;
    }

    @Override
    public Node putKeyValue(Object key, IndexNode value, Comparator compare, boolean locked) {
      AbstractIndexNode subIndex;
      try {
        subIndex = (AbstractIndexNode)this.tail.createNode();
      } catch (Exception ex) {
        return null;
      }
      subIndex.put((FObject)value);
      TreeNode newNode = new TreeNode(key, subIndex, 1, 1, this, this);
      return newNode;
    }
    
    @Override
    public Node bulkLoad(ArrayList<IndexNode> items, Integer start, Integer end, F keyExtractor) {
      if ( end < start ) {
        return this;
      }
      AbstractNode tree = this;
      Integer middle = start + ((Double)Math.floor((end-start+1)/2)).intValue();
      tree = (AbstractNode) tree.putKeyValue(keyExtractor.f(items.get(middle)), items.get(middle), null, false); 
      tree.left = (AbstractNode) tree.left.bulkLoad(items, start, middle-1, keyExtractor);
      tree.right = (AbstractNode) tree.right.bulkLoad(items, middle+1, end, keyExtractor);
      tree.size += tree.left.size+tree.right.size;
      return tree;
    }
}
