package me.thosea.badoptimizations.config;

import me.thosea.badoptimizations.platform.PlatformMethods;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Config {
	public static final Logger LOGGER = LoggerFactory.getLogger("BadOptimizations");
	public static final Path FILE = PlatformMethods.INSTANCE.getConfigFolder().resolve("badoptimizations.txt");
	public static final int CURRENT_CONFIG_VER = 6;

	public static final boolean lightmapCaching;
	public static final int lightmapTimeForUpdate;

	public static final boolean skyColorCaching;
	public static final int skyColorTimeForUpdate;

	public static final boolean debugRendererDisableIfNotNeeded;
	public static final boolean particleManagerOptimization;
	public static final boolean toastOptimizations;
	public static final boolean skyAngleCaching;
	public static final boolean entityRendererCaching;
	public static final boolean blockEntityRendererCaching;
	public static final boolean entityFlagCaching;
	public static final boolean removeRedundantFovCalcs;
	public static final boolean removeTutorialIfNotDemo;

	public static final boolean showF3Text;
	public static final boolean ignoreIncompatibilities;
	public static final boolean ignoreCacheHooks;
	public static final boolean logConfig;

	public static final boolean isError;

	private Config() {}
	public static void init() {}

	// mozilla's regex guide is awesome:
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_expressions/Cheatsheet
	// this is only used in generateConfig, but has to be before teh static block
	@SuppressWarnings("RegExpRedundantEscape")
	private static final Pattern VALUE_PATTERN = Pattern.compile("\\{(.*)\\}");

	static {
		ConfigLoader loader = new ConfigLoader();
		int ver = loader.version;
		if(ver > CURRENT_CONFIG_VER) {
			LOGGER.warn("Config version is newer than supported, this may cause issues" +
					" (supported: {}, found: {})", CURRENT_CONFIG_VER, ver);
		} else if(ver < CURRENT_CONFIG_VER) {
			LOGGER.info("Upgrading config from version {} to supported version {}", ver, CURRENT_CONFIG_VER);
		} else if(!loader.isNewFile) {
			LOGGER.info("Config version: {}", CURRENT_CONFIG_VER);
		}

		// config version 2 (v2.1.1)
		ignoreIncompatibilities = loader.bool("ignore_mod_incompatibilities", ver >= 2);
		if(ignoreIncompatibilities) loader.incompats.ignoreIncompatibilities();
		ignoreCacheHooks = loader.bool("ignore_mod_cache_hooks", ver >= 5);
		logConfig = loader.bool("log_config", ver >= 2);
		if(logConfig) loader.dumpConfig();

		lightmapCaching = loader.optimization("enable_lightmap_caching");
		lightmapTimeForUpdate = loader.number("lightmap_time_change_needed_for_update");

		skyColorCaching = loader.optimization("enable_sky_color_caching");
		skyColorTimeForUpdate = loader.number("skycolor_time_change_needed_for_update");

		debugRendererDisableIfNotNeeded = loader.optimization("enable_debug_renderer_disable_if_not_needed");

		particleManagerOptimization = loader.optimization("enable_particle_manager_optimization");
		toastOptimizations = loader.optimization("enable_toast_optimizations");
		skyAngleCaching = loader.optimization("enable_sky_angle_caching_in_worldrenderer");
		entityRendererCaching = loader.optimization("enable_entity_renderer_caching");
		blockEntityRendererCaching = loader.optimization("enable_block_entity_renderer_caching");
		entityFlagCaching = loader.optimization("enable_entity_flag_caching");
		removeRedundantFovCalcs = loader.optimization("enable_remove_redundant_fov_calculations");
		removeTutorialIfNotDemo = loader.optimization("enable_remove_tutorial_if_not_demo");

		showF3Text = loader.bool("show_f3_text");

		// config v3 removed the fps string optimization, nothing to do
		// config v4 only changes comments
		// config v5 adds caching hooks and changes debug hud comment
		// config v6 only changes comments

		isError = loader.wasError();

		if(!isError && (loader.isNewFile || ver < CURRENT_CONFIG_VER)) {
			ArrayDeque<Object> values = new ArrayDeque<>(List.of(
					lightmapCaching,
					lightmapTimeForUpdate,
					skyColorCaching,
					skyColorTimeForUpdate,
					debugRendererDisableIfNotNeeded,
					particleManagerOptimization,
					toastOptimizations,
					skyAngleCaching,
					entityRendererCaching,
					blockEntityRendererCaching,
					entityFlagCaching,
					removeRedundantFovCalcs,
					removeTutorialIfNotDemo,
					showF3Text,
					ignoreIncompatibilities,
					ignoreCacheHooks,
					logConfig,
					CURRENT_CONFIG_VER
			));
			writeConfig(generateConfig(values));
		}
	}

	public static String generateConfig(@Nullable ArrayDeque<Object> values) {
		String template;
		try(InputStream stream = PlatformMethods.INSTANCE.read("bo-config-template.txt")) {
			template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch(Exception e) {
			throw new RuntimeException("Failed to read config template", e);
		}
		Matcher matcher = VALUE_PATTERN.matcher(template);
		StringBuilder builder = new StringBuilder();
		while(matcher.find()) {
			if(values == null) {
				// just use the default value
				matcher.appendReplacement(builder, matcher.group(1));
			} else {
				matcher.appendReplacement(builder, values.removeFirst().toString());
			}
		}
		matcher.appendTail(builder);
		return builder.toString();
	}

	public static void writeConfig(String config) {
		try {
			LOGGER.info("Saving config file with version {}", CURRENT_CONFIG_VER);
			Path parent = FILE.getParent();
			if(!Files.isDirectory(parent)) {
				Files.createDirectories(parent);
			}
			Files.writeString(FILE, config, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch(IOException e) {
			LOGGER.error("Failed to write config to {}", FILE, e);
		}
	}
}