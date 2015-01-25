package com.grid.app;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import processing.core.PApplet;

import com.google.common.collect.Lists;
import com.grid.structs.geo.Point2D;
import com.grid.structs.geo.QuadTree;
import com.grid.structs.geo.QuadTree.Node;
import com.grid.structs.models.Environment;
import com.grid.structs.models.Message;
import com.grid.structs.models.World;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class GeoApplet extends PApplet {
	private static final long serialVersionUID = 4379813956010720623L;

	ThreadSafeUnfoldingMap map;
	
	private class ThreadSafeUnfoldingMap extends UnfoldingMap {
		public ThreadSafeUnfoldingMap(PApplet arg0) {
			super(arg0);
		}

		public void setMarkers(List<Marker> markers) {
			getMarkerManager(0).setMarkers(markers);
		}
	}
	
	private void withNodes(List<Message> values) {
		final List<Marker> markers = Lists.newArrayList();
		for (Message node : values) {
			final SimplePointMarker marker = new SimplePointMarker(new Location(node.getLocation().x(), node.getLocation().y()));
			marker.setColor(color(255, 255, 255, 100));
			marker.setStrokeColor(color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), 100));
			marker.setStrokeWeight(4);
			marker.setRadius(10);
			markers.add(marker);
			
			final SimplePolygonMarker envMarker = new SimplePolygonMarker();
			final Environment env = new Environment(node.getLocation());
			final List<Point2D> envCorners = env.deriveCorners();
			for (Point2D envCorner : envCorners) {
				envMarker.addLocation((float)envCorner.x(), (float)envCorner.y());
			}
			envMarker.setColor(color(255, 255, 255, 100));
			markers.add(envMarker);
		}
		map.setMarkers(markers);
	}
	
	private synchronized void refresh() {
		withNodes(World.tryRestoreTree("../../" + World.FSYNC_FILENAME).getValues());
	}
	
	public void setup() {
		size(800, 600, OPENGL);
		smooth();

		map = new ThreadSafeUnfoldingMap(this);
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
	}

	public void draw() {
		background(0);
		map.draw();
		refresh();
		map.updateMap();
	}
}