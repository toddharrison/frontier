package com.goodformentertainment.canary.frontier.api;

import java.awt.Point;
import java.awt.Rectangle;

import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;

public interface IFrontierManager {
	Rectangle getBlockBounds(World world);
	
	void setBlockBounds(World world, Point minPoint, Point maxPoint);
	
	Rectangle getRegionBounds(World world);
	
	void setRegionBounds(World world, Point minPoint, Point maxPoint);
	
	boolean clear(World world);
	
	boolean inWilderness(Location location);
	
	void resetAllWildernesses();
}
