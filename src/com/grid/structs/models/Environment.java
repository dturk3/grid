package com.grid.structs.models;

import com.grid.structs.geo.Interval1D;
import com.grid.structs.geo.Interval2D;
import com.grid.structs.geo.Point2D;

public class Environment {
	private static final double RANGE = 0.1000;
	
	private final Point2D mLocation;
	private final Interval2D mArea;
	
	public Environment(final Point2D location) {
		mLocation = location;
		mArea = Environment.deriveArea(getLocation());
	}
	
	private static Interval2D deriveArea(final Point2D location) {
		return new Interval2D(new Interval1D(location.x() - RANGE, location.x() + RANGE), 
							  new Interval1D(location.y() - RANGE, location.y() + RANGE));
	}

	public Point2D getLocation() {
		return mLocation;
	}

	public Interval2D getArea() {
		return mArea;
	}
}
