package me.thosea.badoptimizations.other;

import me.thosea.badoptimizations.utils.PlatformMethods;
import org.apache.commons.lang3.function.FailableRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import static me.thosea.badoptimizations.utils.PlatformMethods.isModLoaded;

public final class Config {
	private Config() {}

	public static final Logger LOGGER = LoggerFactory.getLogger("BadOptimizations");
	public static final Path FILE = PlatformMethods.getConfigFolder().resolve("badoptimizations.txt");
	public static final int CURRENT_CONFIG_VER = 4;

	public static boolean enable_lightmap_caching = true;
	public static int lightmap_time_change_needed_for_update = 80;

	public static boolean enable_sky_color_caching = true;
	public static int skycolor_time_change_needed_for_update = 3;

	public static boolean enable_debug_renderer_disable_if_not_needed = true;
	public static boolean enable_particle_manager_optimization = true;
	public static boolean enable_toast_optimizations = true;
	public static boolean enable_sky_angle_caching_in_worldrenderer = true;
	public static boolean enable_entity_renderer_caching = true;
	public static boolean enable_block_entity_renderer_caching = true;
	public static boolean enable_entity_flag_caching = true;
	public static boolean enable_remove_redundant_fov_calculations = true;
	public static boolean enable_remove_tutorial_if_not_demo = true;

	public static boolean show_f3_text = true;
	public static boolean ignore_mod_compatibilities = false;
	public static boolean log_config = true;

	public static void load() {
		if(Files.exists(FILE)) {
			LOGGER.info("Loading config file");
			doOrCrash(Config::loadConfig, e -> {
				// TODO: does passing Exception as object still log the stack trace?
				LOGGER.error("Failed to load config from " + FILE + ". " +
						"If you need to, you can delete the file to generate a new one.", e);
			});
		} else {
			doOrCrash(Config::writeConfig, e -> {
				LOGGER.error("Failed to write default config to " + FILE, e);
			});
			applyIncompatibilities();
		}
	}

	private static void doOrCrash(FailableRunnable<Exception> action, Consumer<Exception> onFail) {
		try {
			action.run();
		} catch(Exception e) {
			onFail.accept(e);
			System.exit(1);
		}
	}

	private static void loadConfig() throws Exception {
		Properties prop = new Properties();

		try(InputStream stream = Files.newInputStream(FILE)) {
			prop.load(stream);
		}

		int ver = num(prop, "config_version");
		if(ver > CURRENT_CONFIG_VER) {
			LOGGER.warn("Config version is newer than supported, this may cause issues" +
					" (supported: {}, found: {})", CURRENT_CONFIG_VER, ver);
		} else if(ver < CURRENT_CONFIG_VER) {
			LOGGER.info("Upgrading config from version {} to supported version {}", ver, CURRENT_CONFIG_VER);
		} else {
			LOGGER.info("Config version: {}", CURRENT_CONFIG_VER);
		}

		if(ver >= 2) {
			// Config version 2 (v2.1.1)
			ignore_mod_compatibilities = bool(prop, "ignore_mod_incompatibilities");

			if(log_config = bool(prop, "log_config")) {
				LOGGER.info("BadOptimizations config dump:");
				prop.forEach((key, value) -> {
					LOGGER.info("{}: {}", key, value);
				});
			}
		}

		lightmap_time_change_needed_for_update = num(prop, "lightmap_time_change_needed_for_update");
		enable_lightmap_caching = bool(prop, "enable_lightmap_caching")
				&& lightmap_time_change_needed_for_update > 1;

		enable_sky_color_caching = bool(prop, "enable_sky_color_caching");
		skycolor_time_change_needed_for_update = num(prop, "skycolor_time_change_needed_for_update");

		enable_debug_renderer_disable_if_not_needed = bool(prop, "enable_debug_renderer_disable_if_not_needed");
		enable_particle_manager_optimization = bool(prop, "enable_particle_manager_optimization");
		enable_toast_optimizations = bool(prop, "enable_toast_optimizations");
		enable_sky_angle_caching_in_worldrenderer = bool(prop, "enable_sky_angle_caching_in_worldrenderer");
		enable_entity_renderer_caching = bool(prop, "enable_entity_renderer_caching");
		enable_block_entity_renderer_caching = bool(prop, "enable_block_entity_renderer_caching");
		enable_entity_flag_caching = bool(prop, "enable_entity_flag_caching");
		enable_remove_redundant_fov_calculations = bool(prop, "enable_remove_redundant_fov_calculations");
		enable_remove_tutorial_if_not_demo = bool(prop, "enable_remove_tutorial_if_not_demo");

		show_f3_text = bool(prop, "show_f3_text");

		// Config v3 removed the fps string optimization, nothing to do
		// Config v4 only rephrases comments

		if(ver < CURRENT_CONFIG_VER) {
			writeConfig();
		}

		applyIncompatibilities();
	}

	private static boolean bool(Properties prop, String option) {
		String str = prop.getProperty(option);

		if(str == null) {
			throw new IllegalStateException("Config option " + option + " not found.");
		}

		if(str.equalsIgnoreCase("true")) {
			return true;
		} else if(str.equalsIgnoreCase("false")) {
			return false;
		} else {
			throw new IllegalStateException("Config option " + option + " is not \"true\" or \"false\" (\"" + str + "\").");
		}
	}

	@SuppressWarnings("DuplicateBranchesInSwitch")
	private static boolean hasIncompatibilities(String option) {
		List<String> incompatibilities = switch(option) {
			case "enable_entity_renderer_caching" -> List.of("twilightforest", "bedrockskinutility", "lazyyyyy");
			case "enable_block_entity_renderer_caching" -> List.of("lazyyyyy");
			case "enable_sky_color_caching" -> List.of("polytone");
			case "enable_lightmap_caching" -> List.of("polytone");
			case "enable_entity_flag_caching" -> List.of("biomeswevegone", "performant");
			case "enable_remove_redundant_fov_calculations" -> List.of("camera_lock_on");
			default -> null;
		};
		if(incompatibilities == null)
			return false;

		for(String mod : incompatibilities) {
			if(isModLoaded(mod)) {
				LOGGER.info("Disabling {} because mod \"{}\" is present", option, mod);
				return true;
			}
		}

		return false;
	}

	private static int num(Properties prop, String option) {
		String str = prop.getProperty(option);

		if(str == null) {
			throw new IllegalStateException("Config option " + option + " not found.");
		}

		int result;

		try {
			result = Integer.parseInt(str);
		} catch(Exception e) {
			throw new IllegalStateException("Config option " + option + " is not a valid number (\"" + str + "\").");
		}

		if(result < 0) {
			throw new IllegalStateException("Config option " + option + " is negative (" + str + ")");
		}

		return result;
	}

	private static void applyIncompatibilities() {
		if(ignore_mod_compatibilities) return;

		enable_lightmap_caching = enable_lightmap_caching &&
				!hasIncompatibilities("enable_lightmap_caching");
		enable_sky_color_caching = enable_sky_color_caching &&
				!hasIncompatibilities("enable_sky_color_caching");
		enable_debug_renderer_disable_if_not_needed = enable_debug_renderer_disable_if_not_needed &&
				!hasIncompatibilities("enable_debug_renderer_disable_if_not_needed");
		enable_particle_manager_optimization = enable_particle_manager_optimization &&
				!hasIncompatibilities("enable_particle_manager_optimization");
		enable_toast_optimizations = enable_toast_optimizations &&
				!hasIncompatibilities("enable_toast_optimizations");
		enable_sky_angle_caching_in_worldrenderer = enable_sky_angle_caching_in_worldrenderer &&
				!hasIncompatibilities("enable_sky_angle_caching_in_worldrenderer");
		enable_entity_renderer_caching = enable_entity_renderer_caching &&
				!hasIncompatibilities("enable_entity_renderer_caching");
		enable_block_entity_renderer_caching = enable_block_entity_renderer_caching &&
				!hasIncompatibilities("enable_block_entity_renderer_caching");
		enable_entity_flag_caching = enable_entity_flag_caching &&
				!hasIncompatibilities("enable_entity_flag_caching");
		enable_remove_redundant_fov_calculations = enable_remove_redundant_fov_calculations &&
				!hasIncompatibilities("enable_remove_redundant_fov_calculations");
		enable_remove_tutorial_if_not_demo = enable_remove_tutorial_if_not_demo &&
				!hasIncompatibilities("enable_remove_tutorial_if_not_demo");
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
				enable_lightmap_caching,
				lightmap_time_change_needed_for_update,
				enable_sky_color_caching,
				skycolor_time_change_needed_for_update,
				enable_debug_renderer_disable_if_not_needed,
				enable_particle_manager_optimization,
				enable_toast_optimizations,
				enable_sky_angle_caching_in_worldrenderer,
				enable_entity_renderer_caching,
				enable_block_entity_renderer_caching,
				enable_entity_flag_caching,
				enable_remove_redundant_fov_calculations,
				enable_remove_tutorial_if_not_demo,
				show_f3_text,
				ignore_mod_compatibilities,
				log_config,
				CURRENT_CONFIG_VER
		);

		Files.writeString(FILE, data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}
}