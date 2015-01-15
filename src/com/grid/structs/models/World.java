package com.grid.structs.models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.grid.structs.geo.Interval1D;
import com.grid.structs.geo.Interval2D;
import com.grid.structs.geo.Point2D;
import com.grid.structs.geo.QuadTree;

public class World {
	public static final String FSYNC_FILENAME = "world.tree";
	
	public static final Interval2D BOUNDS = new Interval2D(new Interval1D(-180.0, 180.0),
														   new Interval1D(-90.0, 90.0));
	private final QuadTree<Double, Message> mMessages;
	final ObjectOutputStream mOutputStream;

	
	public World() throws IOException {
		mMessages = tryRestoreTree();
		mOutputStream = new ObjectOutputStream(new FileOutputStream(FSYNC_FILENAME));
//		startFsyncThread();
	}
	
	public Message publish(final Point2D location, final String publisher, final String message) {
		final Message resource = Message.create(publisher, message, location);
		mMessages.insert(resource.getLocation().x(), resource.getLocation().y(), resource);
		return resource;
	}
	
	public List<Message> feed(final Environment env) {
		return mMessages.query2D(env.getArea());
	}
	
//	private void startFsyncThread() {
//		final Thread fsyncThread = new Thread() {
//			@Override
//			public void run() {
//				try {
//					mOutputStream.writeObject(mMessages);
//					Thread.sleep(30000);
//				} catch (Exception e) {
//					// No big deal!
//				}
//			}
//		};
//		fsyncThread.start();
//	}
	
	@SuppressWarnings("unchecked")
	private QuadTree<Double, Message> tryRestoreTree() {
		try {
			return (QuadTree<Double, Message>) new ObjectInputStream(new FileInputStream(FSYNC_FILENAME)).readObject();
		}
		catch (Exception e) {
			return new QuadTree<Double, Message>();
		}
	}
}
