package com.grid.spike;

import java.util.Random;

import org.apache.commons.lang3.RandomUtils;

import com.grid.structs.geo.Point2D;
import com.grid.structs.models.Environment;
import com.grid.structs.models.Publisher;
import com.grid.structs.models.World;

public class Spike {
	public static void main(String[] args) throws InterruptedException {
		final Point2D myLocation = new Point2D(-0.1276250, 51.5033630);
		final Point2D anotherLocation = new Point2D(-0.1276251, 51.5033631);

		final World world = new World();
		final Environment myEnv = new Environment(myLocation);
		for (int i = 0; i < 300; i++) {
			if (RandomUtils.nextInt(0, 3) == 2) {
				world.publish(myEnv.getLocation(), Publisher.generateName(), Publisher.generateName());
				world.publish(anotherLocation, Publisher.generateName(), Publisher.generateName());
			}
			System.out.println(world.feed(myEnv));
			Thread.sleep(1000);
		}
	}
}
