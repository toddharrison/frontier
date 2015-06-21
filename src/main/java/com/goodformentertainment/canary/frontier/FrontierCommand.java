package com.goodformentertainment.canary.frontier;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;

import com.goodformentertainment.canary.frontier.api.impl.FrontierManager;

public class FrontierCommand implements CommandListener {
	private final FrontierManager manager;
	
	public FrontierCommand(final FrontierManager manager) {
		this.manager = manager;
	}
	
	@Command(aliases = {
		"frontier"
	}, description = "Get help for Frontier", permissions = {
		"frontier.command"
	}, toolTip = "/frontier")
	public void help(final MessageReceiver caller, final String[] parameters) {
		if (caller instanceof Player) {
			final Player player = caller.asPlayer();
			player.message("Frontier");
			player.message("Usage: /frontier <info | set | clear | reset>");
		}
	}
	
	@Command(aliases = {
		"info"
	}, parent = "frontier", description = "Info on the frontier boundaries", permissions = {
		"frontier.command.info"
	}, toolTip = "/frontier info")
	public void info(final MessageReceiver caller, final String[] parameters) {
		if (caller instanceof Player) {
			final Player player = caller.asPlayer();
			final World world = player.getWorld();
			final Area bounds = manager.getBlockBounds(world);
			if (bounds == null) {
				player.message("There is no frontier in this world");
			} else {
				final int[] points = RegionUtil.areaToPointsArray(bounds);
				// bounds in interval notation
				player.message("Frontier: min = [" + points[0] + ", " + points[1] + "] and max = ["
						+ points[2] + ", " + points[3] + "]");
				if (manager.inWilderness(player.getLocation())) {
					player.message("You are currently in the wilderness");
				} else {
					player.message("You are currently in civilization");
				}
			}
		}
	}
	
	@Command(aliases = {
		"set"
	}, parent = "frontier", description = "Set the frontier boundaries", permissions = {
		"frontier.command.set"
	}, toolTip = "/frontier set <minX minZ maxX maxZ>")
	public void set(final MessageReceiver caller, final String[] parameters) {
		if (caller instanceof Player) {
			final Player player = caller.asPlayer();
			try {
				if (parameters.length == 5) {
					final World world = player.getWorld();
					
					final int minX = Integer.parseInt(parameters[1]);
					final int minZ = Integer.parseInt(parameters[2]);
					final int maxX = Integer.parseInt(parameters[3]);
					final int maxZ = Integer.parseInt(parameters[4]);
					
					final Point minPoint = new Point(minX, minZ);
					final Point maxPoint = new Point(maxX, maxZ);
					final Area regionBounds = manager.setBlockBounds(world, minPoint, maxPoint);
					
					final int[] points = RegionUtil.areaToPointsArray(regionBounds);
					
					player.message("Set the frontier to ");
					player.message("Frontier: min = [" + points[0] + ", " + points[1] + "] and max = ["
							+ points[2] + ", " + points[3] + "]");
				} else {
					player.message("Usage: /frontier set <minX minZ maxX maxZ>");
				}
			} catch (final NumberFormatException e) {
				player.message("Usage: /frontier set <minX minZ maxX maxZ>");
			}
		}
	}
	
	@Command(aliases = {
		"clear"
	}, parent = "frontier", description = "Clear the frontier boundaries", permissions = {
		"frontier.command.clear"
	}, toolTip = "/frontier clear")
	public void clear(final MessageReceiver caller, final String[] parameters) {
		if (caller instanceof Player) {
			final Player player = caller.asPlayer();
			final World world = player.getWorld();
			if (manager.clear(world)) {
				player.message("Removed the frontier from " + world.getName());
			} else {
				player.message("No frontier exists in " + world.getName());
			}
		}
	}
	
	@Command(aliases = {
		"reset"
	}, parent = "frontier", description = "Reset the frontier", permissions = {
		"frontier.command.reset"
	}, toolTip = "/frontier reset")
	public void reset(final MessageReceiver caller, final String[] parameters) {
		if (caller instanceof Player) {
			final Player player = caller.asPlayer();
			manager.resetAllWildernesses();
			player.message("Reset the frontier for all unloaded managed worlds");
		}
	}
}
