package com.grid.structs.models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;

import com.grid.structs.geo.Interval1D;
import com.grid.structs.geo.Interval2D;
import com.grid.structs.geo.Point2D;
import com.grid.structs.geo.QuadTree;
import com.grid.structs.geo.QuadTree.Node;

public class World {
	public static final String FSYNC_FILENAME = "world.tree";
	
	public static final Interval2D BOUNDS = new Interval2D(new Interval1D(-180.0, 180.0),
														   new Interval1D(-90.0, 90.0));
	private final QuadTree<Double, Message> mMessages;
	
	public World() throws IOException {
		mMessages = tryRestoreTree();
		startFsyncThread();
	}
	
	public Message publish(final Point2D location, final String publisher, final String message) {
		final Message resource = Message.create(publisher, message, location);
		mMessages.insert(resource.getLocation().x(), resource.getLocation().y(), resource);
		return resource;
	}
	
	public List<Message> feed(final Environment env) {
		return mMessages.query2D(env.getArea());
	}
	
	private void startFsyncThread() {
		final Thread fsyncThread = new Thread() {
			@Override
			public void run() {
				while(true) {
					ObjectOutputStream stream = null;
					try {
						stream = new ObjectOutputStream(new FileOutputStream(FSYNC_FILENAME));
						stream.writeObject(mMessages.getNodes());
						Thread.sleep(30000);
					} catch (Exception e) {
						System.out.println(e);
					}
					finally {
						try {
							stream.close();
						} catch (Exception e) {
							// No big deal!
						}
					}
				}
			}
		};
		fsyncThread.start();
	}
	
	@SuppressWarnings("unchecked")
	private QuadTree<Double, Message> tryRestoreTree() {
		final QuadTree<Double, Message> tree = new QuadTree<Double, Message>();
		try {
			final Set<Node> nodes = (Set<Node>) new ObjectInputStream(new FileInputStream(FSYNC_FILENAME)).readObject();
			for (Node node : nodes) {
				for (Message value : (List<Message>) node.getValues()) {
					tree.insert(node.getX(), node.getY(), value);
				}
			}
			return tree;
		}
		catch (Exception e) {
			return tree;
		}
	}
}
