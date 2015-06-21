package com.goodformentertainment.canary.frontier;

public class Point {
	public int x;
	public int z;
	
	public Point(final int x, final int z) {
		this.x = x;
		this.z = z;
	}
	
	@Override
	public boolean equals(final Object o) {
		boolean equal = false;
		if (o instanceof Point) {
			final Point p = (Point) o;
			equal = x == p.x && z == p.z;
		}
		return equal;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[x:");
		sb.append(x);
		sb.append(", z:");
		sb.append(z);
		sb.append("]");
		return sb.toString();
	}
}
