package foam.dao.index;

import foam.core.AbstractFObject;
import foam.core.ClassInfo;
import foam.core.ClassInfoImpl;

public abstract class AbstractPlan implements Plan{
    
    protected Integer cost;
    
    public AbstractPlan() {
        this.cost = 0;
    }
    
    public AbstractPlan(Integer cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return this.getClass().getName()+"(cost="+this.cost+")"; 
    }

}
