package com.goodformentertainment.canary.frontier.api;

import com.goodformentertainment.canary.frontier.Area;
import com.goodformentertainment.canary.frontier.Point;
import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;

public interface IFrontierManager {
    Area getBlockBounds(World world);

    Area setBlockBounds(World world, Point minPoint, Point maxPoint);

    Area getRegionBounds(World world);

    void setRegionBounds(World world, Point minPoint, Point maxPoint);

    boolean clear(World world);

    boolean inWilderness(Location location);

    void resetAllWildernesses();
}
