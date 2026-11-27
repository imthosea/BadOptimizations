package me.thosea.badoptimizations.config;

import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Properties;
import java.util.function.Function;

import static me.thosea.badoptimizations.config.Config.FILE;
import static me.thosea.badoptimizations.config.Config.LOGGER;

// first package private ever used in history?
final class ConfigLoader {
	private final Properties properties;
	private Properties defaultProperties = null; /* lazy */
	private boolean error = false;

	public final int version;
	public final boolean isNewFile;
	public final ModIncompatibilities incompats = new ModIncompatibilities();

	ConfigLoader() {
		if(Files.exists(FILE)) {
			this.isNewFile = false;
			this.properties = new Properties();
			try(InputStream stream = Files.newInputStream(FILE)) {
				this.properties.load(stream);
			} catch(Exception e) {
				loadError(e);
			}
		} else {
			loadDefaultConfig();
			this.properties = this.defaultProperties;
			this.isNewFile = true;
		}
		this.version = number("config_version");
	}

	public <T> T get(String name, boolean loadCondition, Function<String, T> transform) {
		if(loadCondition && !error && properties != defaultProperties) {
			String value = properties.getProperty(name);
			if(value == null) {
				loadError(new IllegalStateException("Config option " + name + " not found"));
			} else try {
				return transform.apply(value);
			} catch(Exception e) {
				loadError(e);
			}
		}
		loadDefaultConfig();
		String def = defaultProperties.getProperty(name);
		if(def == null) {
			throw new AssertionError("Default config is missing " + name + ", this is a bug");
		}
		try {
			return transform.apply(def);
		} catch(Exception e) {
			throw new AssertionError("Default config gave error, this is a bug", e);
		}
	}

	public boolean bool(String name, boolean loadCondition) {
		return get(name, loadCondition, value -> {
			if(value.equalsIgnoreCase("true")) {
				return true;
			} else if(value.equalsIgnoreCase("false")) {
				return false;
			} else {
				throw new IllegalStateException("Config option " + name + " is not \"true\" or \"false\" (\"" + value + "\")");
			}
		});
	}
	public boolean bool(String name) {
		return bool(name, /*load*/ true);
	}

	public boolean optimization(String name, boolean loadCondition) {
		return bool(name, loadCondition) && !incompats.isIncompatible(name);
	}
	public boolean optimization(String name) {
		return optimization(name, /*load*/ true);
	}

	public int number(String name) {
		return get(name, /*load*/ true, value -> {
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
		});
	}

	public boolean wasError() {
		return error;
	}

	public void dumpConfig() {
		if(error) return;
		LOGGER.info("BadOptimizations config dump:");
		properties.forEach((key, value) -> {
			LOGGER.info("{}: {}", key, value);
		});
	}

	private void loadError(Exception e) {
		LOGGER.error("Error loading the config from {}. " +
				"If you need to, you can delete the file to generate a new one.", FILE, e);
		LOGGER.error("Using default values for now");
		error = true;
	}
	private void loadDefaultConfig() {
		if(defaultProperties != null) return;
		try {
			String config = Config.generateConfig(/*values*/ null);
			defaultProperties = new Properties();
			defaultProperties.load(new StringReader(config));
		} catch(Exception e) {
			// checked exceptions sucks!!
			throw new AssertionError("Failed to parse default config, this is a bug", e);
		}
	}
}