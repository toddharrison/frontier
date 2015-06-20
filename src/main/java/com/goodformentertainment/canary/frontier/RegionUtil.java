package com.goodformentertainment.canary.frontier;

import java.awt.Rectangle;

public final class RegionUtil {
	public static final int BLOCKS_PER_CHUNK = 16;
	public static final int CHUNKS_PER_REGION = 32;
	public static final int BLOCKS_PER_REGION = CHUNKS_PER_REGION * BLOCKS_PER_CHUNK;
	
	// Region 0:0 = [0:0, 512:512)
	// Region -1:-1 = [-512:-512, 0:0)
	
	public static Rectangle pointsToRectangle(final int minX, final int minZ, final int maxX,
			final int maxZ) {
		return new Rectangle(minX, minZ, maxX - minX, maxZ - minZ);
	}
	
	public static int[] rectangleToPoints(final Rectangle bounds) {
		return new int[] {
				bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height
		};
	}
	
	public static Rectangle regionToBlockBounds(final Rectangle regionBounds) {
		Rectangle blockBounds = null;
		if (regionBounds != null) {
			final int[] regionPoints = rectangleToPoints(regionBounds);
			
			final int minX = fromRegionToBlock(regionPoints[0], true);
			final int minZ = fromRegionToBlock(regionPoints[1], true);
			final int maxX = fromRegionToBlock(regionPoints[2], false);
			final int maxZ = fromRegionToBlock(regionPoints[3], false);
			
			blockBounds = pointsToRectangle(minX, minZ, maxX, maxZ);
		}
		return blockBounds;
	}
	
	public static int fromBlockToRegion(final int blockLoc) {
		return (int) Math.floor(blockLoc / (double) BLOCKS_PER_REGION);
	}
	
	public static int fromRegionToBlock(final int regionLoc, final boolean roundDown) {
		if (roundDown) {
			return regionLoc * BLOCKS_PER_REGION;
		} else {
			return (regionLoc + 1) * BLOCKS_PER_REGION - 1;
		}
	}
}
