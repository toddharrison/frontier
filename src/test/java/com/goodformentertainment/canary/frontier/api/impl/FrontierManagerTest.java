package com.goodformentertainment.canary.frontier.api.impl;

import com.goodformentertainment.canary.frontier.Area;
import com.goodformentertainment.canary.frontier.FrontierConfig;
import com.goodformentertainment.canary.frontier.FrontierPlugin;
import com.goodformentertainment.canary.frontier.Point;
import com.goodformentertainment.canary.frontier.RegionUtil;
import net.canarymod.api.world.DimensionType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;
import net.canarymod.logger.Logman;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

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
        expect(mockConfig.getRegionBounds(mockWorld)).andReturn(new Area(-1, -1, 1, 1));

        replayAll();

        assertEquals(new Area(-512, -512, 1023, 1023), manager.getBlockBounds(mockWorld));

        verifyAll();
    }

    @Test
    public void testGetRegionBounds() {
        expect(mockConfig.getRegionBounds(mockWorld)).andReturn(new Area(-1, -1, 2, 2));

        replayAll();

        assertEquals(new Area(-1, -1, 2, 2), manager.getRegionBounds(mockWorld));

        verifyAll();
    }

    @Test
    public void testInWilderness() {
        expect(mockWorld.getType()).andReturn(DimensionType.NORMAL).anyTimes();
        expect(mockWorld.getName()).andReturn("default").anyTimes();
        expect(mockConfig.getRegionBounds(mockWorld)).andReturn(new Area(-1, -1, 1, 1)).anyTimes();

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
        mockConfig.setRegionBounds(mockWorld, new Area(-10, 5, 200, 50));

        replayAll();

        manager.setRegionBounds(mockWorld, new Point(-10, 5), new Point(200, 50));

        verifyAll();
    }

    @Test
    public void testSetBlockBounds() {
        mockConfig.setRegionBounds(mockWorld, -2, -1, 1, 0);

        replayAll();

        manager.setBlockBounds(mockWorld, new Point(-513, -1), new Point(512, 50));

        verifyAll();
    }

    @Test
    public void testMaths() {
        assertEquals(-2, RegionUtil.fromBlockToRegion(-513));
        assertEquals(-1, RegionUtil.fromBlockToRegion(-512));
        assertEquals(-1, RegionUtil.fromBlockToRegion(-1));
        assertEquals(0, RegionUtil.fromBlockToRegion(0));
        assertEquals(0, RegionUtil.fromBlockToRegion(1));
        assertEquals(0, RegionUtil.fromBlockToRegion(511));
        assertEquals(1, RegionUtil.fromBlockToRegion(512));
        assertEquals(1, RegionUtil.fromBlockToRegion(513));
        assertEquals(1, RegionUtil.fromBlockToRegion(1023));
        assertEquals(2, RegionUtil.fromBlockToRegion(1024));

        assertEquals(-1024, RegionUtil.fromRegionToBlock(-2, true));
        assertEquals(-512, RegionUtil.fromRegionToBlock(-1, true));
        assertEquals(0, RegionUtil.fromRegionToBlock(0, true));
        assertEquals(512, RegionUtil.fromRegionToBlock(1, true));
        assertEquals(1024, RegionUtil.fromRegionToBlock(2, true));

        assertEquals(-1025, RegionUtil.fromRegionToBlock(-3, false));
        assertEquals(-513, RegionUtil.fromRegionToBlock(-2, false));
        assertEquals(-1, RegionUtil.fromRegionToBlock(-1, false));
        assertEquals(511, RegionUtil.fromRegionToBlock(0, false));
        assertEquals(1023, RegionUtil.fromRegionToBlock(1, false));
        assertEquals(1535, RegionUtil.fromRegionToBlock(2, false));
    }

    @Test
    public void test() {
        final Area regionArea = new Area(-1, -1, 3, 4);
        final Area blockArea = new Area(-512, -512, 2047, 2559);

        mockConfig.setRegionBounds(mockWorld, regionArea.min.x, regionArea.min.z, regionArea.max.x,
                regionArea.max.z);
        expect(mockConfig.getRegionBounds(mockWorld)).andReturn(regionArea);

        replayAll();

        final Area regionBounds = manager.setBlockBounds(mockWorld, new Point(-512, -512), new Point(
                2045, 2555));
        assertEquals(blockArea, regionBounds);

        final Area blockBounds = manager.getBlockBounds(mockWorld);
        assertEquals(blockArea, blockBounds);

        verifyAll();
    }
}
