package com.goodformentertainment.canary.frontier.api.impl;

import com.goodformentertainment.canary.frontier.Area;
import com.goodformentertainment.canary.frontier.FrontierConfig;
import com.goodformentertainment.canary.frontier.FrontierPlugin;
import com.goodformentertainment.canary.frontier.Point;
import com.goodformentertainment.canary.frontier.RegionUtil;
import com.goodformentertainment.canary.frontier.api.IFrontierManager;
import net.canarymod.Canary;
import net.canarymod.api.world.World;
import net.canarymod.api.world.WorldManager;
import net.canarymod.api.world.position.Location;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrontierManager implements IFrontierManager {
    private final FrontierConfig config;

    public FrontierManager(final FrontierConfig config) {
        this.config = config;
    }

    @Override
    public Area getBlockBounds(final World world) {
        return RegionUtil.regionToBlockBounds(config.getRegionBounds(world));
    }

    @Override
    public Area setBlockBounds(final World world, final Point minPoint, final Point maxPoint) {
        final int regionMinX = RegionUtil.fromBlockToRegion(minPoint.x);
        final int regionMinZ = RegionUtil.fromBlockToRegion(minPoint.z);
        final int regionMaxX = RegionUtil.fromBlockToRegion(maxPoint.x);
        final int regionMaxZ = RegionUtil.fromBlockToRegion(maxPoint.z);
        config.setRegionBounds(world, regionMinX, regionMinZ, regionMaxX, regionMaxZ);

        final int blockMinX = RegionUtil.fromRegionToBlock(regionMinX, true);
        final int blockMinZ = RegionUtil.fromRegionToBlock(regionMinZ, true);
        final int blockMaxX = RegionUtil.fromRegionToBlock(regionMaxX, false);
        final int blockMaxZ = RegionUtil.fromRegionToBlock(regionMaxZ, false);
        return RegionUtil.pointsToArea(blockMinX, blockMinZ, blockMaxX, blockMaxZ);
    }

    @Override
    public Area getRegionBounds(final World world) {
        return config.getRegionBounds(world);
    }

    @Override
    public void setRegionBounds(final World world, final Point minPoint, final Point maxPoint) {
        final Area bounds = new Area(minPoint, maxPoint);
        config.setRegionBounds(world, bounds);
    }

    @Override
    public boolean clear(final World world) {
        return config.removeManagedWorld(world);
    }

    @Override
    public boolean inWilderness(final Location location) {
        final Area regionBounds = config.getRegionBounds(location.getWorld());
        final Area blockBounds = RegionUtil.regionToBlockBounds(regionBounds);
        return !blockBounds.contains(new Point(location.getBlockX(), location.getBlockZ()));
    }

    @Override
    public void resetAllWildernesses() {
        final WorldManager worldManager = Canary.getServer().getWorldManager();
        final Collection<String> loadedWorldNames = Arrays.asList(worldManager.getLoadedWorldsNames());
        for (final String worldFqName : config.getManagedWorldNames()) {
            if (loadedWorldNames.contains(worldFqName)) {
                FrontierPlugin.LOG.warn("Unable to reset wilderness in " + worldFqName);
            } else if (worldFqName != null && !worldFqName.isEmpty()) {
                final String worldName = worldFqName.substring(0, worldFqName.indexOf("_"));

                try {
                    final File worldsDir = new File("worlds");
                    if (worldsDir.exists()) {
                        final File regionDir = new File(worldsDir, worldName + "/" + worldFqName + "/region");
                        if (regionDir.exists()) {

                            final Area regionBounds = config.getRegionBounds(worldFqName);

                            final Pattern pattern = Pattern.compile("^r\\.(-?\\d+)\\.(-?\\d+)\\.mca$");
                            for (final String filename : regionDir.list()) {
                                final Matcher matcher = pattern.matcher(filename);
                                if (matcher.matches()) {
                                    final int x = Integer.parseInt(matcher.group(1));
                                    final int z = Integer.parseInt(matcher.group(2));
                                    if (regionBounds.min.x > x || regionBounds.max.x < x || regionBounds.min.z > z
                                            || regionBounds.max.z < z) {
                                        final File regionFile = new File(regionDir, filename);
                                        if (regionFile.delete()) {
                                            FrontierPlugin.LOG.info("Deleted " + filename + " for " + worldFqName);
                                        } else {
                                            FrontierPlugin.LOG.error("Failed deleting " + filename + " for "
                                                    + worldFqName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (final Exception e) {
                    FrontierPlugin.LOG.error("Error deleting region files", e);
                }
            }
        }
    }
}
