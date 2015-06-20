package com.goodformentertainment.canary.frontier;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.canarymod.api.world.World;
import net.canarymod.config.Configuration;
import net.visualillusionsent.utils.PropertiesFile;

public class FrontierConfig {
	private static final String MANAGED_WORLDS = "worlds";
	private static final String WORLD_FRONTIER = "frontier.";
	
	private final PropertiesFile cfg;
	
	public FrontierConfig(final FrontierPlugin plugin) {
		cfg = Configuration.getPluginConfig(plugin);
	}
	
	public Collection<String> getManagedWorldNames() {
		final Set<String> worlds = new HashSet<String>();
		for (final String world : cfg.getStringArray(MANAGED_WORLDS, new String[0])) {
			worlds.add(world);
		}
		return worlds;
	}
	
	public void addManagedWorld(final World world) {
		final String[] worldNames = cfg.getStringArray(MANAGED_WORLDS);
		if (!Arrays.asList(worldNames).contains(world.getFqName())) {
			final String[] newNames = new String[worldNames.length + 1];
			System.arraycopy(worldNames, 0, newNames, 0, worldNames.length);
			newNames[worldNames.length + 1] = world.getFqName();
			
			cfg.setStringArray(MANAGED_WORLDS, newNames);
			cfg.save();
		}
	}
	
	public boolean removeManagedWorld(final World world) {
		boolean removed = false;
		final String[] worldNames = cfg.getStringArray(MANAGED_WORLDS);
		final List<String> nameList = Arrays.asList(worldNames);
		if (nameList.remove(world.getFqName())) {
			final String[] newNames = nameList.toArray(new String[nameList.size()]);
			cfg.setStringArray(MANAGED_WORLDS, newNames);
			cfg.removeKey(WORLD_FRONTIER + world.getFqName());
			cfg.save();
			removed = true;
		}
		return removed;
	}
	
	public Rectangle getRegionBounds(final World world) {
		final int[] points = cfg.getIntArray(WORLD_FRONTIER + world.getFqName(), (int[]) null);
		
		Rectangle bounds = null;
		if (points != null && points.length == 4) {
			final int xMin = points[0];
			final int xMax = points[1];
			final int zMin = points[2];
			final int zMax = points[3];
			
			bounds = RegionUtil.pointsToRectangle(xMin, zMin, xMax - xMin, zMax - zMin);
		}
		
		return bounds;
	}
	
	public void setRegionBounds(final World world, final Rectangle bounds) {
		addManagedWorld(world);
		final int points[] = RegionUtil.rectangleToPoints(bounds);
		cfg.setIntArray(WORLD_FRONTIER + world.getFqName(), points);
		cfg.save();
	}
	
	public void setRegionBounds(final World world, final int xMin, final int xMax, final int zMin,
			final int zMax) {
		addManagedWorld(world);
		final int points[] = RegionUtil.pointsToArray(xMin, xMax, zMin, zMax);
		cfg.setIntArray(WORLD_FRONTIER + world.getFqName(), points);
		cfg.save();
	}
	
	public String getLoggingLevel() {
		String level = null;
		final String key = "log.level";
		if (cfg.containsKey(key)) {
			level = cfg.getString(key);
		}
		return level;
	}
}
