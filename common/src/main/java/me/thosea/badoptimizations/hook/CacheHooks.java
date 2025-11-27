package me.thosea.badoptimizations.hook;

import me.thosea.badoptimizations.config.Config;
import me.thosea.badoptimizations.utils.PlatformMethods;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static me.thosea.badoptimizations.config.Config.LOGGER;

/**
 * Some mods add extra conditions that could cause the lightmap or sky color to change
 * (i.e. gamma utils has a config option to change night vision strength)
 * which malfunction when BO cancels their updates.
 *
 * This class lets mods hook extra conditions that would cause a lightmap/skycolor update
 * so there doesn't need to be a hard incompatibility instead.
 */
public final class CacheHooks {
	private CacheHooks() {}

	public static final String ROOT_KEY = "badoptimizations:cache_hooks";
	public static final String COMMON_KEY = "common";
	public static final String LIGHTMAP_KEY = "lightmap";
	public static final String SKYCOLOR_KEY = "skycolor";

	public record CacheHookEntry(
			@Nullable BooleanSupplier commonHook,
			@Nullable BooleanSupplier lightmapHook,
			@Nullable BooleanSupplier skyColorHook
	) {}

	/** hooks common to both lightmap and sky color */
	private static final BooleanSupplier[] COMMON_COLOR_HOOKS;
	/** hooks for lightmap updates */
	private static final BooleanSupplier[] LIGHTMAP_HOOKS;
	/** hooks for sky color updates */
	private static final BooleanSupplier[] SKYCOLOR_HOOKS;

	public static void init() {}
	static {
		if(Config.ignoreCacheHooks) {
			LOGGER.warn("Ignore mod cache hooks is enabled!");
			COMMON_COLOR_HOOKS = new BooleanSupplier[0];
			LIGHTMAP_HOOKS = new BooleanSupplier[0];
			SKYCOLOR_HOOKS = new BooleanSupplier[0];
		} else {
			List<BooleanSupplier> commonHooks = new ArrayList<>();
			List<BooleanSupplier> lightmapHooks = new ArrayList<>();
			List<BooleanSupplier> skyColorHooks = new ArrayList<>();

			PlatformMethods.getModCacheHooks().forEach(hook -> {
				if(hook.commonHook != null) commonHooks.add(hook.commonHook);
				if(hook.lightmapHook != null) lightmapHooks.add(hook.lightmapHook);
				if(hook.skyColorHook != null) skyColorHooks.add(hook.skyColorHook);
			});

			BooleanSupplier[] dummy = new BooleanSupplier[0];
			COMMON_COLOR_HOOKS = commonHooks.toArray(dummy);
			LIGHTMAP_HOOKS = lightmapHooks.toArray(dummy);
			SKYCOLOR_HOOKS = skyColorHooks.toArray(dummy);
		}
	}

	public static boolean invokeCommon() {
		if(Config.ignoreCacheHooks) return false;
		for(BooleanSupplier hook : COMMON_COLOR_HOOKS) {
			if(hook.getAsBoolean())
				return true;
		}
		return false;
	}
	public static boolean invokeLightmap() {
		if(Config.ignoreCacheHooks) return false;
		for(BooleanSupplier hook : LIGHTMAP_HOOKS) {
			if(hook.getAsBoolean())
				return true;
		}
		return false;
	}
	public static boolean invokeSkyColor() {
		if(Config.ignoreCacheHooks) return false;
		for(BooleanSupplier hook : SKYCOLOR_HOOKS) {
			if(hook.getAsBoolean())
				return true;
		}
		return false;
	}
}