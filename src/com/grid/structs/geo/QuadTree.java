package com.grid.structs.geo;

import java.util.List;

import com.google.common.collect.Lists;


public class QuadTree<Key extends Comparable<Key>, Value>  {
    private Node root;

    // helper node data type
    private class Node {
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
    }


  /***********************************************************************
    *  Insert (x, y) into appropriate quadrant
    ***********************************************************************/
    public void insert(Double x, Double y, Value value) {
        root = insert(root, x, y, value);
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
            System.out.println("    (" + h.x + ", " + h.y + ") " + h.values);
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


   /*************************************************************************
    *  test client
    *************************************************************************/
    public static void main(String[] args) {
//        int M = Integer.parseInt(args[0]);   // queries
//        int N = Integer.parseInt(args[1]);   // points
    	
    	int M = 10;
    	int N = 5000;

        QuadTree<Integer, String> st = new QuadTree<Integer, String>();

        // insert N random points in the unit square
        for (int i = 0; i < N; i++) {
            Double x = (double) (100 * Math.random());
            Double y = (double) (100 * Math.random());
            System.out.println("(" + x + ", " + y + ")");
            st.insert(x, y, "P" + i);
        }
        System.out.println("Done preprocessing " + N + " points");

        // do some range searches
        for (int i = 0; i < M; i++) {
            Integer xmin = (int) (100 * Math.random());
            Integer ymin = (int) (100 * Math.random());
            Integer xmax = xmin + (int) (10 * Math.random());
            Integer ymax = ymin + (int) (20 * Math.random());
            Interval1D intX = new Interval1D(xmin, xmax);
            Interval1D intY = new Interval1D(ymin, ymax);
            Interval2D rect = new Interval2D(intX, intY);
            System.out.println(rect + ": ");
            st.query2D(rect);
        }
    }

}