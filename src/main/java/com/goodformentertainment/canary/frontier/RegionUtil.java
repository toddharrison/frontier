package com.goodformentertainment.canary.frontier;

import java.awt.Rectangle;

public final class RegionUtil {
	public static final int BLOCKS_PER_CHUNK = 16;
	public static final int CHUNKS_PER_REGION = 32;
	public static final int BLOCKS_PER_REGION = CHUNKS_PER_REGION * BLOCKS_PER_CHUNK;
	
	// Region 0:0 = [0:0, 512:512)
	// Region -1:-1 = [-512:-512, 0:0)
	
	public static Rectangle pointsToRectangle(final int minX, final int maxX, final int minZ,
			final int maxZ) {
		return new Rectangle(minX, minZ, maxX - minX, maxZ - minZ);
	}
	
	public static int[] rectangleToPoints(final Rectangle bounds) {
		return new int[] {
				bounds.x, bounds.x + bounds.width, bounds.y, bounds.y + bounds.height
		};
	}
	
	public static int[] pointsToArray(final int xMin, final int xMax, final int zMin, final int zMax) {
		return new int[] {
				xMin, zMin, xMax - xMin, zMax - zMin
		};
	}
	
	public static Rectangle regionToBlockBounds(final Rectangle regionBounds) {
		Rectangle blockBounds = null;
		if (regionBounds != null) {
			final int[] regionPoints = RegionUtil.rectangleToPoints(regionBounds);
			
			final int minX = regionPoints[0] * BLOCKS_PER_REGION;
			final int maxX = (regionPoints[1] + 1) * BLOCKS_PER_REGION - 1;
			final int minZ = regionPoints[2] * BLOCKS_PER_REGION;
			final int maxZ = (regionPoints[3] + 1) * BLOCKS_PER_REGION - 1;
			
			blockBounds = RegionUtil.pointsToRectangle(minX, maxX, minZ, maxZ);
		}
		return blockBounds;
	}
}
