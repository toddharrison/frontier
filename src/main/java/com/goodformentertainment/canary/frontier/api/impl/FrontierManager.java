package com.goodformentertainment.canary.frontier.api.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.canarymod.Canary;
import net.canarymod.api.world.World;
import net.canarymod.api.world.WorldManager;
import net.canarymod.api.world.position.Location;

import com.goodformentertainment.canary.frontier.FrontierConfig;
import com.goodformentertainment.canary.frontier.FrontierPlugin;
import com.goodformentertainment.canary.frontier.RegionUtil;
import com.goodformentertainment.canary.frontier.api.IFrontierManager;

public class FrontierManager implements IFrontierManager {
	private final FrontierConfig config;
	
	public FrontierManager(final FrontierConfig config) {
		this.config = config;
	}
	
	@Override
	public Rectangle getBlockBounds(final World world) {
		return RegionUtil.regionToBlockBounds(config.getRegionBounds(world));
	}
	
	@Override
	public Rectangle setBlockBounds(final World world, final Point minPoint, final Point maxPoint) {
		final int regionMinX = RegionUtil.fromBlockToRegion(minPoint.x);
		final int regionMinZ = RegionUtil.fromBlockToRegion(minPoint.y);
		final int regionMaxX = RegionUtil.fromBlockToRegion(maxPoint.x);
		final int regionMaxZ = RegionUtil.fromBlockToRegion(maxPoint.y);
		config.setRegionBounds(world, regionMinX, regionMinZ, regionMaxX, regionMaxZ);
		
		final int blockMinX = RegionUtil.fromRegionToBlock(regionMinX, true);
		final int blockMinZ = RegionUtil.fromRegionToBlock(regionMinZ, true);
		final int blockMaxX = RegionUtil.fromRegionToBlock(regionMaxX, false);
		final int blockMaxZ = RegionUtil.fromRegionToBlock(regionMaxZ, false);
		return RegionUtil.pointsToRectangle(blockMinX, blockMinZ, blockMaxX, blockMaxZ);
	}
	
	@Override
	public Rectangle getRegionBounds(final World world) {
		return config.getRegionBounds(world);
	}
	
	@Override
	public void setRegionBounds(final World world, final Point minPoint, final Point maxPoint) {
		final Rectangle bounds = new Rectangle(minPoint);
		bounds.add(maxPoint);
		config.setRegionBounds(world, bounds);
	}
	
	@Override
	public boolean clear(final World world) {
		return config.removeManagedWorld(world);
	}
	
	@Override
	public boolean inWilderness(final Location location) {
		final Rectangle regionBounds = config.getRegionBounds(location.getWorld());
		final Rectangle blockBounds = RegionUtil.regionToBlockBounds(regionBounds);
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
							
							final Rectangle regionBounds = config.getRegionBounds(worldFqName);
							
							final Pattern pattern = Pattern.compile("^r\\.(-?\\d+)\\.(-?\\d+)\\.mca$");
							for (final String filename : regionDir.list()) {
								final Matcher matcher = pattern.matcher(filename);
								if (matcher.matches()) {
									final int x = Integer.parseInt(matcher.group(1));
									final int z = Integer.parseInt(matcher.group(2));
									if (regionBounds.x > x || regionBounds.x + regionBounds.width < x
											|| regionBounds.y > z || regionBounds.y + regionBounds.height < z) {
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
