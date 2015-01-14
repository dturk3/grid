package com.grid.structs.geo;



/**
 * The <tt>Interval2D</tt> class represents a closed two-dimensional interval,
 * which represents all points (x, y) with both xleft <= x <= xright and yleft
 * <= y <= right. Two-dimensional intervals are immutable: their values cannot
 * be changed after they are created. The class <code>Interval2D</code> includes
 * methods for checking whether a two-dimensional interval contains a point and
 * determining whether two two-dimensional intervals intersect.
 * <p>
 * For additional documentation, see <a href="/algs4/12oop">Section 1.2</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Interval2D {
	private final Interval1D x;
	private final Interval1D y;

	/**
	 * Initializes a two-dimensional interval.
	 * 
	 * @param x
	 *            the one-dimensional interval of x-coordinates
	 * @param y
	 *            the one-dimensional interval of y-coordinates
	 */
	public Interval2D(Interval1D x, Interval1D y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Does this two-dimensional interval intersect that two-dimensional
	 * interval?
	 * 
	 * @param that
	 *            the other two-dimensional interval
	 * @return true if this two-dimensional interval intersects that
	 *         two-dimensional interval; false otherwise
	 */
	public boolean intersects(Interval2D that) {
		if (!this.x.intersects(that.x))
			return false;
		if (!this.y.intersects(that.y))
			return false;
		return true;
	}

	/**
	 * Does this two-dimensional interval contain the point p?
	 * 
	 * @param p
	 *            the two-dimensional point
	 * @return true if this two-dimensional interval contains the point p; false
	 *         otherwise
	 */
	public boolean contains(Point2D p) {
		return x.contains(p.x()) && y.contains(p.y());
	}

	/**
	 * Returns the area of this two-dimensional interval.
	 * 
	 * @return the area of this two-dimensional interval
	 */
	public double area() {
		return x.length() * y.length();
	}
	
	public Interval1D getX() {
		return this.x;
	}
	
	public Interval1D getY() {
		return this.x;
	}

	/**
	 * Returns a string representation of this two-dimensional interval.
	 * 
	 * @return a string representation of this two-dimensional interval in the
	 *         form [xleft, xright] x [yleft, yright]
	 */
	public String toString() {
		return x + " x " + y;
	}
	
}
