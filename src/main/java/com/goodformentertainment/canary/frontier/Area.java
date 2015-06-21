package com.goodformentertainment.canary.frontier;

public class Area {
	public Point min;
	public Point max;
	
	public Area() {
		this(0, 0, 0, 0);
	}
	
	public Area(final int minX, final int minZ, final int maxX, final int maxZ) {
		min = new Point(minX, minZ);
		max = new Point(maxX, maxZ);
	}
	
	public Area(final Point min, final Point max) {
		this.min = min;
		this.max = max;
	}
	
	public boolean contains(final Point point) {
		return point.x >= min.x && point.x <= max.x && point.z >= min.z && point.z <= max.z;
	}
	
	@Override
	public boolean equals(final Object o) {
		boolean equal = false;
		if (o instanceof Area) {
			final Area a = (Area) o;
			equal = min.equals(a.min) && max.equals(a.max);
		}
		return equal;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{ min: ");
		sb.append(min);
		sb.append(", max: ");
		sb.append(max);
		sb.append(" }");
		return sb.toString();
	}
}
