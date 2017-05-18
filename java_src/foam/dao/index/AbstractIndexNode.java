package foam.dao.index;

public abstract class AbstractIndexNode implements IndexNode {
    
    protected Index index;
    
    public AbstractIndexNode() {
    }
    
    public AbstractIndexNode(Index index) {
        this.index = index;
    }
    
}
