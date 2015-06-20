package com.goodformentertainment.canary.frontier.api.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;

import net.canarymod.api.world.DimensionType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;
import net.canarymod.logger.Logman;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.goodformentertainment.canary.frontier.FrontierConfig;
import com.goodformentertainment.canary.frontier.FrontierPlugin;

public class FrontierManagerTest extends EasyMockSupport {
	private FrontierConfig mockConfig;
	private Logman mockLog;
	private World mockWorld;
	
	private FrontierManager manager;
	
	@Before
	public void init() {
		mockConfig = createMock(FrontierConfig.class);
		mockLog = createMock(Logman.class);
		FrontierPlugin.LOG = mockLog;
		mockWorld = createMock(World.class);
		
		manager = new FrontierManager(mockConfig);
	}
	
	@Test
	public void testGetBlockBounds() {
		expect(mockConfig.getRegionBounds(mockWorld)).andReturn(new Rectangle(-1, -1, 2, 2));
		
		replayAll();
		
		assertEquals(new Rectangle(-512, -512, 1535, 1535), manager.getBlockBounds(mockWorld));
		
		verifyAll();
	}
	
	@Test
	public void testGetRegionBounds() {
		expect(mockConfig.getRegionBounds(mockWorld)).andReturn(new Rectangle(-1, -1, 2, 2));
		
		replayAll();
		
		assertEquals(new Rectangle(-1, -1, 2, 2), manager.getRegionBounds(mockWorld));
		
		verifyAll();
	}
	
	@Test
	public void testInWilderness() {
		expect(mockWorld.getType()).andReturn(DimensionType.NORMAL).anyTimes();
		expect(mockWorld.getName()).andReturn("default").anyTimes();
		expect(mockConfig.getRegionBounds(mockWorld)).andReturn(new Rectangle(-1, -1, 2, 2)).anyTimes();
		
		replayAll();
		
		assertFalse(manager.inWilderness(new Location(mockWorld, 0, 0, 0, 0, 0)));
		assertFalse(manager.inWilderness(new Location(mockWorld, 1, 0, 1, 0, 0)));
		assertFalse(manager.inWilderness(new Location(mockWorld, -1, 0, -1, 0, 0)));
		assertFalse(manager.inWilderness(new Location(mockWorld, -512, 0, -512, 0, 0)));
		
		assertTrue(manager.inWilderness(new Location(mockWorld, -513, 0, -513, 0, 0)));
		
		verifyAll();
	}
	
	@Test
	public void testSetRegionBounds() {
		mockConfig.setRegionBounds(mockWorld, new Rectangle(-10, 5, 210, 45));
		
		replayAll();
		
		manager.setRegionBounds(mockWorld, new Point(-10, 5), new Point(200, 50));
		
		verifyAll();
	}
	
	@Test
	public void testSetBlockBounds() {
		mockConfig.setRegionBounds(mockWorld, -2, 1, -1, 0);
		
		replayAll();
		
		manager.setBlockBounds(mockWorld, new Point(-513, -1), new Point(512, 50));
		
		verifyAll();
	}
}