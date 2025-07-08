package me.thosea.badoptimizations.config;

import me.thosea.badoptimizations.utils.PlatformMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.thosea.badoptimizations.config.Config.LOGGER;

public final class ModIncompatibilities {
	public static final String KEY = "badoptimizations:incompatibilities";

	private record IncompatibleMod(boolean builtIn, String mod) {}

	private final Map<String, List<IncompatibleMod>> incompats = new HashMap<>();
	private boolean ignoreIncompatibilities;

	public ModIncompatibilities() {
		builtIn("enable_entity_renderer_caching", "twilightforest", "bedrockskinutility", "lazyyyyy");
		builtIn("enable_block_entity_renderer_caching", "lazyyyyy");
		builtIn("enable_sky_color_caching", "polytone");
		builtIn("enable_lightmap_caching", "polytone");
		builtIn("enable_entity_flag_caching", "biomeswevegone", "performant");
		builtIn("enable_remove_redundant_fov_calculations", "camera_lock_on");

		PlatformMethods.getModIncompatibilities().forEach((id, options) -> {
			IncompatibleMod mod = new IncompatibleMod(false, id);
			options.forEach(option -> getList(option).add(mod));
		});
	}

	private void builtIn(String option, String... mods) {
		for(String mod : mods) {
			if(PlatformMethods.isModLoaded(mod)) {
				getList(option).add(new IncompatibleMod(true, mod));
			}
		}
	}

	private List<IncompatibleMod> getList(String option) {
		return incompats.computeIfAbsent(option, k -> new ArrayList<>());
	}

	public void ignoreIncompatibilities() {
		this.ignoreIncompatibilities = true;
		LOGGER.warn("Ignore incompatibilities is enabled!");
	}

	public boolean isIncompatible(String option) {
		if(ignoreIncompatibilities) return false;

		List<IncompatibleMod> list = incompats.get(option);
		if(list == null) return false;

		for(ModIncompatibilities.IncompatibleMod mod : list) {
			if(mod.builtIn) {
				LOGGER.info(
						"Disabling {} because mod \"{}\" is incompatible with it (built-in)",
						option, mod.mod
				);
			} else {
				LOGGER.info(
						"Disabling {} because mod \"{}\" marks itself as incompatible with it (external)",
						option, mod.mod
				);
			}
		}
		return true;
	}
}