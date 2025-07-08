package me.thosea.badoptimizations.utils.fabric;

import me.thosea.badoptimizations.config.ModIncompatibilities;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.CustomValue.CvType;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.thosea.badoptimizations.config.Config.LOGGER;

public final class PlatformMethodsImpl {
	private PlatformMethodsImpl() {}

	public static String getVersion() {
		return ownModContainer().getMetadata().getVersion().getFriendlyString();
	}

	public static Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static boolean isModLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}

	public static InputStream streamConfigTemplate() throws IOException {
		return Files.newInputStream(ownModContainer()
				.findPath("bo-config-template.txt")
				.orElseThrow(() -> new RuntimeException("BadOptimizations config template not found")));
	}

	private static ModContainer ownModContainer() {
		return FabricLoader.getInstance()
				.getModContainer("badoptimizations")
				.orElseThrow(() -> new RuntimeException("BadOptimizations mod container not found"));
	}

	public static Map<String, List<String>> getModIncompatibilities() {
		Map<String, List<String>> result = new HashMap<>(1);
		for(ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			ModMetadata meta = mod.getMetadata();
			String id = meta.getId();

			CustomValue object = meta.getCustomValue(ModIncompatibilities.KEY);
			if(object == null) continue;

			if(object.getType() != CvType.ARRAY) {
				LOGGER.warn("Mod {} specifies invalid BadOptimizations incompatibilities, ignoring", id);
				LOGGER.warn("JSON is not an array");
				continue;
			}

			List<String> entries = new ArrayList<>();
			for(CustomValue entry : object.getAsArray()) {
				if(entry == null || entry.getType() != CvType.STRING) {
					LOGGER.warn("Mod {} specifies invalid BadOptimizations incompatibilities", id);
					LOGGER.warn("JSON array contained non-string component");
					continue;
				}
				entries.add(entry.getAsString());
			}

			result.put(id, entries);
		}
		return result;
	}
}