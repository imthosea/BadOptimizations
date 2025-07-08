package me.thosea.badoptimizations.config;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

import static me.thosea.badoptimizations.config.Config.FILE;
import static me.thosea.badoptimizations.config.Config.LOGGER;

public final class ConfigLoadContext {
	public final ModIncompatibilities incompats = new ModIncompatibilities();
	public final int version;
	@Nullable public final Properties properties;

	public ConfigLoadContext() {
		if(Files.exists(FILE)) {
			this.properties = loadProp();
		} else {
			this.properties = null;
		}

		this.version = intOrDefault("config_version", Config.CURRENT_CONFIG_VER);
	}

	private Properties loadProp() {
		try {
			Properties prop = new Properties();
			try(InputStream stream = Files.newInputStream(FILE)) {
				prop.load(stream);
				return prop;
			}
		} catch(Exception e) {
			// TODO: does passing Exception as object still log the stack trace?
			LOGGER.error("Failed to load config from " + FILE + ". " +
					"If you need to, you can delete the file to generate a new one.", e);
			System.exit(1);
			return null;
		}
	}

	public boolean boolOrDefault(String name, boolean def) {
		String value = str(name);
		if(value == null) {
			return def;
		} else if(value.equalsIgnoreCase("true")) {
			return true;
		} else if(value.equalsIgnoreCase("false")) {
			return false;
		} else {
			throw new IllegalStateException("Config option " + name + " is not \"true\" or \"false\" (\"" + name + "\")");
		}
	}

	public int intOrDefault(String name, int def) {
		String value = str(name);
		if(value == null) return def;

		int result;
		try {
			result = Integer.parseInt(value);
		} catch(NumberFormatException e) {
			throw new IllegalStateException("Config number " + name + " is not a valid number (\"" + value + "\")");
		}
		if(result < 0) {
			throw new IllegalStateException("Config number " + name + " is negative (" + result + ")");
		}
		return result;
	}

	private String str(String name) {
		if(properties == null) return null;

		String str = properties.getProperty(name);
		if(str == null) {
			throw new IllegalStateException("Config option " + name + " not found");
		}
		return str;
	}

	public boolean fromExistingFile() {
		return properties != null;
	}

	public void dumpConfig() {
		if(properties == null) {
			LOGGER.info("BadOptimizations is generating the config with default values");
		} else {
			LOGGER.info("BadOptimizations config dump:");
			properties.forEach((key, value) -> {
				LOGGER.info("{}: {}", key, value);
			});
		}
	}

	public ConfigOptimization optimization(String option) {
		return new ConfigOptimization(this, option, /*loadCondition*/ true);
	}

	public ConfigOptimization optimization(String option, boolean loadCondition) {
		return new ConfigOptimization(this, option, loadCondition);
	}

	public boolean option(String option, boolean def) {
		return option(option, true, def);
	}

	public boolean option(String option, boolean loadCondition, boolean def) {
		if(loadCondition) {
			return boolOrDefault(option, def);
		} else {
			return def;
		}
	}
}