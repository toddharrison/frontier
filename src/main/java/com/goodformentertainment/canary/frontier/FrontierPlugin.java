package com.goodformentertainment.canary.frontier;

import net.canarymod.Canary;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.goodformentertainment.canary.frontier.api.IFrontierManager;
import com.goodformentertainment.canary.frontier.api.impl.FrontierManager;

public class FrontierPlugin extends Plugin {
	public static Logman LOG;
	
	private static FrontierManager manager;
	
	/**
	 * Get the FrontierManager from the FrontierPlugin.
	 * 
	 * @return The FrontierManager.
	 */
	public static IFrontierManager getFrontierManager() {
		return manager;
	}
	
	private FrontierConfig config;
	private FrontierCommand command;
	
	public FrontierPlugin() {
		FrontierPlugin.LOG = getLogman();
	}
	
	@Override
	public boolean enable() {
		boolean success = true;
		
		config = new FrontierConfig(this);
		setLoggingLevel(config.getLoggingLevel());
		
		LOG.info("Enabling " + getName() + " Version " + getVersion());
		LOG.info("Authored by " + getAuthor());
		
		manager = new FrontierManager(config);
		command = new FrontierCommand(manager);
		
		// Canary.hooks().registerListener(regionCleaner, this);
		
		try {
			Canary.commands().registerCommands(command, this, false);
		} catch (final CommandDependencyException e) {
			LOG.error("Error registering commands: ", e);
			success = false;
		}
		
		manager.resetAllWildernesses();
		
		return success;
	}
	
	@Override
	public void disable() {
		LOG.info("Disabling " + getName());
		Canary.commands().unregisterCommands(this);
		Canary.hooks().unregisterPluginListeners(this);
		
		config = null;
		manager = null;
		command = null;
	}
	
	private void setLoggingLevel(final String level) {
		if (level != null) {
			final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			final Configuration config = ctx.getConfiguration();
			final LoggerConfig loggerConfig = config.getLoggerConfig(LOG.getName());
			loggerConfig.setLevel(Level.toLevel(level));
			ctx.updateLoggers();
		}
	}
}
