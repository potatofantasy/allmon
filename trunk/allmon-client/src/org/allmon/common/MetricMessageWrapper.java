package org.allmon.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class defines a data transformation object holding <b>collection</b> metrics data acquired 
 * in monitored application by allmon client API, transformed (aggregated) and sent by allmon client 
 * to allmon server. 
 * Allmon server decodes this object to RawMetric to persist this data in "raw" form in database.
 */
public class MetricMessageWrapper implements Serializable {

    private ArrayList list = new ArrayList(); // TODO check in allmon-client would be compilable in 1.5 to introduce generics

    public boolean add(MetricMessage o) {
        return list.add(o);
    }

    public boolean add(MetricMessageWrapper o) {
        if (o != null) {
            return list.addAll(o.list); // TODO investigate performance!
        }
        return false;
    }
    
    public void clear() {
        list.clear();
    }

    public boolean contains(MetricMessage elem) {
        return list.contains(elem);
    }

    public MetricMessage get(int index) {
        return (MetricMessage)list.get(index);
    }

    public int indexOf(MetricMessage elem) {
        return list.indexOf(elem);
    }

    public Iterator iterator() {
        return list.iterator();
    }

    public Object remove(int index) {
        return list.remove(index);
    }

    public boolean remove(MetricMessage o) {
        return list.remove(o);
    }

    public MetricMessage set(int index, Object element) {
        return (MetricMessage)list.set(index, element);
    }

    public int size() {
        return list.size();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append("[");
            buffer.append(list.get(i).toString());
            buffer.append("], ");
        }
        return buffer.toString();
    }    
    
}
