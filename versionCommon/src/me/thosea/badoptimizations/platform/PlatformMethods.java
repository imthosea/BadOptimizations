package me.thosea.badoptimizations.platform;

import me.thosea.badoptimizations.hook.CacheHooks.CacheHookEntry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface PlatformMethods {
	String getVersion();
	Path getConfigFolder();
	boolean isModLoaded(String id);
	boolean isOnServer();

	InputStream streamConfigTemplate() throws IOException;
	Map<String, List<String>> getModIncompatibilities();
	List<CacheHookEntry> getModCacheHooks();

	PlatformMethods INSTANCE = find();

	static PlatformMethods find() {
		// say these names 10 times fast i dare you
		final String fabricMethods = "me.thosea.badoptimizations.fabric.platform.FabricPlatformMethods";
		final String forgeMethods = "me.thosea.badoptimizations.forge.platform.ForgePlatformMethods";

		// String serviceName = MixinService.getService().getName();
		// ...but this to avoid depending on mixin here:
		String serviceName;
		try {
			var serviceObj = Class.forName("org.spongepowered.asm.service.MixinService")
					.getMethod("getService")
					.invoke(null);
			serviceName = (String) serviceObj.getClass()
					.getMethod("getName")
					.invoke(serviceObj);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		String clazz = null;
		if(serviceName.contains("Fabric")) {
			clazz = fabricMethods;
		} else if(serviceName.contains("ModLauncher")) {
			clazz = forgeMethods;
		} else {
			// we can't rely on this alone because of that one forge-on-fabric mod
			// and that other fabric-on-forge mod
			// reminds me of the picture of the car on the bike
			try {
				Class.forName("net.fabricmc.loader.api.FabricLoader");
				clazz = fabricMethods;
			} catch(ClassNotFoundException ignored) {}
			try {
				Class.forName("net.minecraftforge.fml.ModList");
				clazz = forgeMethods;
			} catch(ClassNotFoundException ignored) {}
			try {
				Class.forName("net.neoforged.fml.ModList");
				clazz = forgeMethods;
			} catch(ClassNotFoundException ignored) {}
			if(clazz == null) {
				throw new RuntimeException("Couldn't find platform methods");
			}
		}

		try {
			// you need to do this or it crashes on neoforge
			// custom class loaders were a mistake!
			return (PlatformMethods) Thread
					.currentThread()
					.getContextClassLoader()
					.loadClass(clazz)
					.getConstructor().newInstance();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}