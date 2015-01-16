package com.grid.structs.geo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class QuadTree<Key extends Comparable<Key>, Value> implements Serializable {
	private static final long serialVersionUID = -2059257062284519390L;
	
	private Node root;
	private final Set<Node> mAllNodes = Sets.newHashSet();

    // helper node data type
    public class Node implements Serializable {
    	private static final long serialVersionUID = -237886367153852095L;
		Double x, y;              // x- and y- coordinates
        Node NW, NE, SE, SW;   // four subtrees
        List<Value> values = Lists.newArrayList();           // associated data

        Node(Double x, Double y, Value value) {
            this.x = x;
            this.y = y;
            this.values.add(value);
        }
        
        public void add(Value value) {
        	values.add(value);
        }
        
        public Double getX() {
        	return x;
        }
        
        public Double getY() {
        	return y;
        }
        public List<Value> getValues() {
        	return values;
        }
               
//        @Override
//        public boolean equals(Object obj) {
//        	if (obj == null) {
//        		return false;
//        	}
//        	if (!Node.class.equals(obj.getClass())) {
//        		return false;
//        	}
//        	Node other = (Node) obj; 
//        	return getX().equals(other.getX()) && getY().equals(other.getY()) && getValues().equals(other.getValues());
//        }
    }


  /***********************************************************************
    *  Insert (x, y) into appropriate quadrant
    ***********************************************************************/
    public void insert(Double x, Double y, Value value) {
        root = insert(root, x, y, value);
        mAllNodes.add(root);
    }

    private Node insert(Node h, Double x, Double y, Value value) {
        if (h == null) return new Node(x, y, value);
        if (eq(x, h.x) && eq(y, h.y)) h.add(value);  // duplicate
        else if ( less(x, h.x) &&  less(y, h.y)) h.SW = insert(h.SW, x, y, value);
        else if ( less(x, h.x) && !less(y, h.y)) h.NW = insert(h.NW, x, y, value);
        else if (!less(x, h.x) &&  less(y, h.y)) h.SE = insert(h.SE, x, y, value);
        else if (!less(x, h.x) && !less(y, h.y)) h.NE = insert(h.NE, x, y, value);
        return h;
    }


  /***********************************************************************
    *  Range search.
    ***********************************************************************/

    public List<Value> query2D(Interval2D rect) {
    	final List<Value> results = Lists.newArrayList();
        return query2D(root, rect, results);
    }

    private List<Value> query2D(Node h, Interval2D rect, List<Value> results) {
        if (h == null) return results;
        double xmin = rect.getX().left();
        double ymin = rect.getY().left();
        double xmax = rect.getX().right();
        double ymax = rect.getY().right();
        if (rect.contains(new Point2D(h.x, h.y)))
        	results.addAll(h.values);
//            System.out.println("    (" + h.x + ", " + h.y + ") " + h.values);
        if ( less(xmin, h.x) &&  less(ymin, h.y)) return query2D(h.SW, rect, results);
        if ( less(xmin, h.x) && !less(ymax, h.y)) return query2D(h.NW, rect, results);
        if (!less(xmax, h.x) &&  less(ymin, h.y)) return query2D(h.SE, rect, results);
        if (!less(xmax, h.x) && !less(ymax, h.y)) return query2D(h.NE, rect, results);
        return results;
    }


   /*************************************************************************
    *  helper comparison functions
    *************************************************************************/

    private boolean less(Double k1, Double k2) { return k1.compareTo(k2) <  0; }
    private boolean eq(Double k1, Double k2) { return k1.compareTo(k2) == 0; }
    
    public Set<Node> getNodes() {
    	return mAllNodes;
	}
}