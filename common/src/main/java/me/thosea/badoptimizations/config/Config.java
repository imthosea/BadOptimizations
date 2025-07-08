package me.thosea.badoptimizations.config;

import me.thosea.badoptimizations.utils.PlatformMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class Config {
	public static final Logger LOGGER = LoggerFactory.getLogger("BadOptimizations");
	public static final Path FILE = PlatformMethods.getConfigFolder().resolve("badoptimizations.txt");
	public static final int CURRENT_CONFIG_VER = 4;

	public static final ConfigOptimization lightmapCaching;
	public static final int lightmapTimeForUpdate;

	public static final ConfigOptimization skyColorCaching;
	public static final int skyColorTimeForUpdate;

	public static final ConfigOptimization debugRendererDisableIfNotNeeded;
	public static final ConfigOptimization particleManagerOptimization;
	public static final ConfigOptimization toastOptimizations;
	public static final ConfigOptimization skyAngleCaching;
	public static final ConfigOptimization entityRendererCaching;
	public static final ConfigOptimization blockEntityRendererCaching;
	public static final ConfigOptimization entityFlagCaching;
	public static final ConfigOptimization removeRedundantFovCalcs;
	public static final ConfigOptimization removeTutorialIfNotDemo;

	public static final boolean showF3Text;
	public static final boolean ignoreIncompatibilities;
	public static final boolean logConfig;

	private Config() {}
	public static void init() {}

	static {
		ConfigLoadContext ctx = new ConfigLoadContext();
		int ver = ctx.version;
		if(ver > CURRENT_CONFIG_VER) {
			LOGGER.warn("Config version is newer than supported, this may cause issues" +
					" (supported: {}, found: {})", CURRENT_CONFIG_VER, ver);
		} else if(ver < CURRENT_CONFIG_VER) {
			LOGGER.info("Upgrading config from version {} to supported version {}", ver, CURRENT_CONFIG_VER);
		} else {
			LOGGER.info("Config version: {}", CURRENT_CONFIG_VER);
		}

		// config version 2 (v2.1.1)
		ignoreIncompatibilities = ctx.option("ignore_mod_incompatibilities", ver >= 2, false);
		if(ignoreIncompatibilities) ctx.incompats.ignoreIncompatibilities();
		logConfig = ctx.option("log_config", ver >= 2, true);
		if(logConfig) ctx.dumpConfig();

		lightmapCaching = ctx.optimization("enable_lightmap_caching");
		lightmapTimeForUpdate = ctx.intOrDefault("lightmap_time_change_needed_for_update", 80);

		skyColorCaching = ctx.optimization("enable_sky_color_caching");
		skyColorTimeForUpdate = ctx.intOrDefault("skycolor_time_change_needed_for_update", 3);

		debugRendererDisableIfNotNeeded = ctx.optimization("enable_debug_renderer_disable_if_not_needed");

		particleManagerOptimization = ctx.optimization("enable_particle_manager_optimization");
		toastOptimizations = ctx.optimization("enable_toast_optimizations");
		skyAngleCaching = ctx.optimization("enable_sky_angle_caching_in_worldrenderer");
		entityRendererCaching = ctx.optimization("enable_entity_renderer_caching");
		blockEntityRendererCaching = ctx.optimization("enable_block_entity_renderer_caching");
		entityFlagCaching = ctx.optimization("enable_entity_flag_caching");
		removeRedundantFovCalcs = ctx.optimization("enable_remove_redundant_fov_calculations");
		removeTutorialIfNotDemo = ctx.optimization("enable_remove_tutorial_if_not_demo");

		showF3Text = ctx.option("show_f3_text", true);

		// config v3 removed the fps string optimization, nothing to do
		// config v4 only rephrases comments

		if(!ctx.fromExistingFile() || ver < CURRENT_CONFIG_VER) {
			try {
				writeConfig();
			} catch(Exception e) {
				LOGGER.error("Failed to write default config to " + FILE, e);
				System.exit(1);
			}
		}
	}

	public static void writeConfig() throws Exception {
		LOGGER.info("Generating config file version {}", CURRENT_CONFIG_VER);

		Path parent = FILE.getParent();
		if(!Files.isDirectory(parent)) {
			Files.createDirectories(parent);
		}

		String template;
		try(InputStream stream = PlatformMethods.streamConfigTemplate()) {
			template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		}

		String data = template.formatted(
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
				logConfig,
				CURRENT_CONFIG_VER
		);

		Files.writeString(FILE, data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}
}