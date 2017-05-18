/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package foam.dao.index;

import foam.core.ClassInfo;
import foam.core.PropertyInfo;
import foam.dao.Sink;
import foam.mlang.predicate.Predicate;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author marco
 */
public class AltPlan extends AbstractPlan {
    
    ArrayList<Plan> subPlans;
    PropertyInfo prop;
    
    public AltPlan () {
        subPlans = new ArrayList();
    }

    public ArrayList<Plan> getSubPlans() {
        return subPlans;
    }

    public void setSubPlans(ArrayList<Plan> subPlans) {
        this.subPlans = subPlans;
    }
    
    public void addSubPlan(Plan subPlan) {
        this.subPlans.add(subPlan);
    }
    
    public PropertyInfo getProp() {
        return prop;
    }

    public void setProp(PropertyInfo prop) {
        this.prop = prop;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
    
    @Override
    public String toString() {
        return this.subPlans.size() <= 1  ?
        "IN(key=" + this.prop.getName() + ", cost=" + this.cost + ", " +
          ", size=" + this.subPlans.size() + ")" :
        "lookup(key=" + this.prop.getName() + ", cost=" + this.cost + ", " +
          this.subPlans.get(0).toString();
        
    }

    @Override
    public void execute(Sink sink, Integer skip, Integer limit, Comparator order, Predicate predicate) {
        for (int i = 0; i < subPlans.size(); i++) {
                subPlans.get(i).execute(sink, skip, limit, order, predicate);
        }
    }
    
}
