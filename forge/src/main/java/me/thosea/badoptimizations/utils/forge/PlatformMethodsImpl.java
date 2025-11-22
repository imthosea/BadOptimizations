package me.thosea.badoptimizations.utils.forge;

import me.thosea.badoptimizations.config.ModIncompatibilities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		return modList().getModFileById(id) != null;
	}

	public static boolean isOnServer() {
		return FMLLoader.getCurrent().getDist() == Dist.DEDICATED_SERVER;
	}

	public static InputStream streamConfigTemplate() throws IOException {
		return modList()
				.getModFileById("badoptimizations")
				.getFile()
				.getContents()
				.openFile("bo-config-template.txt");
	}

	public static Map<String, List<String>> getModIncompatibilities() {
		Map<String, List<String>> result = new HashMap<>(1);

		for(ModInfo mod : modList().getMods()) {
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

	private static <T extends Map<?, ?>> T castMap(Map<?, ?> map) {
		return (T) map;
	}

	private static LoadingModList modList() {
		return FMLLoader.getCurrent().getLoadingModList();
	}

	/*
	public static List<CacheHookEntry> getModHooks() {
		Map<String, List<String>> result = new HashMap<>(1);

		for(ModInfo mod : LoadingModList.get().getMods()) {
			String id = mod.getModId();
			Optional<Object> object = mod.getOwningFile().getConfigElement(CacheHooks.ROOT_KEY);
			if(object.isEmpty()) continue;
			if(!(object.get() instanceof Map<?, ?> map) ||
					!(castMap(map).get("options") instanceof List<?> list)) {
				LOGGER.warn("Mod {} specifies invalid BadOptimizations caching hooks, ignoring", id);
				LOGGER.warn("TOML is not a map containing an object");
				continue;
			}
		}
	}
	 */
}