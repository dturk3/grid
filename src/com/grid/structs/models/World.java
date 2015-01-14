package com.grid.structs.models;

import java.util.List;

import com.grid.structs.geo.Interval1D;
import com.grid.structs.geo.Interval2D;
import com.grid.structs.geo.Point2D;
import com.grid.structs.geo.QuadTree;

public class World {
	public static final Interval2D BOUNDS = new Interval2D(new Interval1D(-180.0, 180.0),
														   new Interval1D(-90.0, 90.0));
	private final QuadTree<Double, Message> mMessages = new QuadTree<Double, Message>();
	
	public Message publish(final Point2D location, final String publisher, final String message) {
		final Message resource = Message.create(publisher, message, location);
		mMessages.insert(resource.getLocation().x(), resource.getLocation().y(), resource);
		return resource;
	}
	
	public List<Message> feed(final Environment env) {
		return mMessages.query2D(env.getArea());
	}
}
