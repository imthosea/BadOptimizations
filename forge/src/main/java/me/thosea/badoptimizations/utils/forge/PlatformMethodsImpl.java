package me.thosea.badoptimizations.utils.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
		return FMLEnvironment.dist.isDedicatedServer();
	}

	public static InputStream streamConfigTemplate() throws IOException {
		return Files.newInputStream(LoadingModList.get()
				.getModFileById("badoptimizations")
				.getFile()
				.findResource("bo-config-template.txt"));
	}
}