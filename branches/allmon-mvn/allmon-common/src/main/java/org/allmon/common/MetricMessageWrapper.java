package org.allmon.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class defines a data transformation object holding <b>collection</b> of metrics data acquired 
 * in monitored application by allmon client API, transformed (aggregated) and sent by allmon client 
 * to allmon server.<br><br>
 * 
 * Allmon server decodes this object to RawMetric to persist this data in "raw" form in a database.
 */
public class MetricMessageWrapper implements Serializable, Iterable<MetricMessage> {

    private ArrayList<MetricMessage> list = new ArrayList<MetricMessage>();

    public MetricMessageWrapper() {
    }

    public MetricMessageWrapper(MetricMessage metricMessage) {
        add(metricMessage);
    }
    
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

    public Iterator<MetricMessage> iterator() {
        return list.iterator();
    }

    public Object remove(int index) {
        return list.remove(index);
    }

    public boolean remove(MetricMessage o) {
        return list.remove(o);
    }

    public MetricMessage set(int index, MetricMessage element) {
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
            buffer.append("\n[").append(list.get(i).toString()).append("], ");
        }
        return buffer.toString();
    }    
    
    public void setPoint(String point) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setPoint(point);
        }
    }
    
}
