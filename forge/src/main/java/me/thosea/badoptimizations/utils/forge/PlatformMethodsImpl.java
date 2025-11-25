package me.thosea.badoptimizations.utils.forge;

import me.thosea.badoptimizations.config.ModIncompatibilities;
import me.thosea.badoptimizations.hook.CacheHooks;
import me.thosea.badoptimizations.hook.CacheHooks.CacheHookEntry;
import me.thosea.badoptimizations.hook.HookCreator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static me.thosea.badoptimizations.config.Config.LOGGER;

public final class PlatformMethodsImpl {
	private PlatformMethodsImpl() {}

	public static String getVersion() {
		return ModList.get().getModContainerById("badoptimizations")
				.map(mod -> mod.getModInfo().getVersion().toString())
				.orElse("[unknown version]");
	}

	public static Path getConfigFolder() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static boolean isModLoaded(String id) {
		return LoadingModList.get().getModFileById(id) != null;
	}

	public static boolean isOnServer() {
		return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
	}

	public static InputStream streamConfigTemplate() throws IOException {
		return Files.newInputStream(LoadingModList.get()
				.getModFileById("badoptimizations")
				.getFile()
				.findResource("bo-config-template.txt"));
	}

	public static Map<String, List<String>> getModIncompatibilities() {
		Map<String, List<String>> result = new HashMap<>(1);

		for(ModInfo mod : LoadingModList.get().getMods()) {
			String id = mod.getModId();
			Optional<Object> object = mod.getOwningFile().getConfigElement(ModIncompatibilities.KEY);
			if(object.isEmpty()) continue;
			if(!(object.get() instanceof Map<?, ?> map) ||
					!(castMap(map).get("options") instanceof List<?> list)) {
				LOGGER.warn("Mod {} specifies invalid BadOptimizations incompatibilities, ignoring", id);
				LOGGER.warn("TOML is not a map containing a string list named options");
				continue;
			}

			List<String> entries = new ArrayList<>();
			for(Object element : list) {
				if(!(element instanceof String entry)) {
					LOGGER.warn("Mod {} specifies invalid BadOptimizations incompatibilities", id);
					LOGGER.warn("TOML options contains non-string value in array");
					continue;
				}
				entries.add(entry);
			}

			result.put(id, entries);
		}

		return result;
	}

	public static List<CacheHookEntry> getModCacheHooks() {
		List<CacheHookEntry> result = new ArrayList<>();
		for(ModInfo mod : LoadingModList.get().getMods()) {
			String id = mod.getModId();
			Optional<Object> object = mod.getOwningFile().getConfigElement(CacheHooks.ROOT_KEY);
			if(object.isEmpty()) continue;
			if(!(object.get() instanceof Map<?, ?> map)) {
				LOGGER.warn("Mod {} specifies invalid BadOptimizations caching hooks, ignoring", id);
				LOGGER.warn("TOML is not a map");
				continue;
			}

			BooleanSupplier common = getEntry(id, map, CacheHooks.COMMON_KEY);
			BooleanSupplier lightmap = getEntry(id, map, CacheHooks.LIGHTMAP_KEY);
			BooleanSupplier skyColor = getEntry(id, map, CacheHooks.SKYCOLOR_KEY);
			if(common == null && lightmap == null && skyColor == null) continue;

			CacheHookEntry entry = new CacheHookEntry(common, lightmap, skyColor);
			result.add(entry);
		}
		return result;
	}

	private static BooleanSupplier getEntry(String modId, Map<?, ?> map, String key) {
		Object value = map.get(key);
		if(value == null) {
			return null;
		} else if(!(value instanceof String string)) {
			LOGGER.warn(HookCreator.INVALID_HOOK_MESSAGE, modId);
			LOGGER.warn("TOML key {} is not a string", key);
			return null;
		} else {
			BooleanSupplier hook = HookCreator.tryCreateHook(modId, string);
			if(hook != null) {
				LOGGER.info("Mod {} added a {} caching hook: {}", modId, key, string);
			}
			return hook;
		}
	}

	private static <T extends Map<?, ?>> T castMap(Map<?, ?> map) {
		return (T) map;
	}
}