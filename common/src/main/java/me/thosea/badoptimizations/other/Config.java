package me.thosea.badoptimizations.other;

import me.thosea.badoptimizations.utils.PlatformMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

import static me.thosea.badoptimizations.utils.PlatformMethods.isModLoaded;

@SuppressWarnings("unused")
public final class Config {
	private Config() {}

	public static final Logger LOGGER = LoggerFactory.getLogger("BadOptimizations");
	public static final File FILE = new File(PlatformMethods.getConfigFolder(), "badoptimizations.txt");
	public static final int CONFIG_VER = 4;

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

	public static void load() {
		if(FILE.exists()) {
			LOGGER.info("Loading config file");

			try {
				loadConfig();
			} catch(Exception e) {
				LOGGER.error("Failed to load config from " + FILE + ". " +
						"If you need to, you can delete the file to generate a new one.", e);
				System.exit(1);
			}
		} else {
			try {
				writeConfig();
			} catch(Exception e) {
				LOGGER.error("Failed to write default config to " + FILE, e);
				System.exit(1);
			}
		}
	}

	private static void loadConfig() throws Exception {
		Properties prop = new Properties();

		try(FileInputStream stream = new FileInputStream(FILE)) {
			prop.load(stream);
		}

		int ver = num(prop, "config_version");
		if(ver > CONFIG_VER) {
			LOGGER.warn("Config version is newer than supported, this may cause issues" +
					" (supported: {}, found: {})", CONFIG_VER, ver);
		} else if(ver < CONFIG_VER) {
			LOGGER.info("Upgrading config from version {} to supported version {}", ver, CONFIG_VER);
		} else {
			LOGGER.info("Config version: {}", CONFIG_VER);
		}

		if(ver >= 2) {
			// Config version 2 (v2.1.1)
			if(!bool(prop, "ignore_mod_incompatibilities")) {
				disableIncompatibleOptions(prop);
			}

			if(bool(prop, "log_config")) {
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

		if(ver < CONFIG_VER) {
			writeConfig();
			loadConfig();
		}
	}

	private static void disableIncompatibleOptions(Properties prop) {
		disableIf(prop, "enable_entity_renderer_caching", List.of("twilightforest", "bedrockskinutility", "lazyyyyy"));
		disableIf(prop, "enable_block_entity_renderer_caching", List.of("lazyyyyy"));

		disableIf(prop, "enable_sky_color_caching", List.of("polytone"));
		disableIf(prop, "enable_lightmap_caching", List.of("polytone"));

		disableIf(prop, "enable_entity_flag_caching", List.of("biomeswevegone", "performant"));

		disableIf(prop, "enable_remove_redundant_fov_calculations", List.of("camera_lock_on"));
	}

	private static void disableIf(Properties prop, String option, List<String> mods) {
		if(!prop.containsKey(option)) {
			LOGGER.warn("Missing option \"{}\"", option);
			return;
		}

		for(String mod : mods) {
			if(isModLoaded(mod)) {
				prop.setProperty(option, "false");
				LOGGER.info("Disabled {} because mod \"{}\" is present.", option, mod);
				break;
			}
		}
	}

	private static boolean bool(Properties prop, String name) {
		String str = prop.getProperty(name);

		if(str == null) {
			throw new IllegalStateException("Config option " + name + " not found.");
		}

		if(str.equalsIgnoreCase("true")) {
			return true;
		} else if(str.equalsIgnoreCase("false")) {
			return false;
		} else {
			throw new IllegalStateException("Config option " + name + " is not \"true\" or \"false\" (\"" + str + "\").");
		}
	}

	private static int num(Properties prop, String name) {
		String str = prop.getProperty(name);

		if(str == null) {
			throw new IllegalStateException("Config option " + name + " not found.");
		}

		int result;

		try {
			result = Integer.parseInt(str);
		} catch(Exception e) {
			throw new IllegalStateException("Config option " + name + " is not a valid number (\"" + str + "\").");
		}

		if(result < 0) {
			throw new IllegalStateException("Config option " + name + " is negative (" + str + ")");
		}

		return result;
	}

	public static void writeConfig() throws Exception {
		LOGGER.info("Generating config file version {}", CONFIG_VER);

		File parent = FILE.getParentFile();
		if(!parent.exists()) {
			if(!parent.mkdirs()) {
				throw new Exception("Failed to create config directory at " + parent);
			}
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
				/*ignore_mod_incompatibilities=*/false,
				/*log_config=*/true,
				CONFIG_VER
		);

		if(FILE.exists()) FILE.delete();
		Files.writeString(FILE.toPath(), data, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	}
}