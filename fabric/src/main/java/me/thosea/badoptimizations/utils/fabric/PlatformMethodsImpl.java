package me.thosea.badoptimizations.utils.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PlatformMethodsImpl {
	private PlatformMethodsImpl() {}

	public static String getVersion() {
		return getModContainer().getMetadata().getVersion().getFriendlyString();
	}

	public static Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static boolean isModLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}

	public static boolean isOnServer() {
		return false; // fabric won't let client-marked mods run on servers
	}

	public static InputStream streamConfigTemplate() throws IOException {
		return Files.newInputStream(getModContainer()
				.findPath("bo-config-template.txt")
				.orElseThrow(() -> new RuntimeException("BadOptimizations config template not found")));
	}

	private static ModContainer getModContainer() {
		return FabricLoader.getInstance()
				.getModContainer("badoptimizations")
				.orElseThrow(() -> new RuntimeException("BadOptimizations mod container not found"));
	}
}