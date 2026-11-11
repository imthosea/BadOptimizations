package me.thosea.badoptimizations.platform;

import me.thosea.badoptimizations.hook.CacheHooks.CacheHookEntry;
import me.thosea.badoptimizations.reflect.BadReflection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface PlatformMethods {
	String getVersion();
	String getMinecraftVersion();
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

		String clazz;
		if(serviceName.contains("Fabric")) {
			clazz = fabricMethods;
		} else if(serviceName.contains("ModLauncher")) {
			clazz = forgeMethods;
		} else if(BadReflection.getClass("net.fabricmc.loader.api.FabricLoader") != null) {
			// we can't rely on this alone because of that one fabric-on-forge mod
			// and that other forge-on-fabric mod
			// reminds me of the picture of the car on the bike
			clazz = fabricMethods;
		} else if(BadReflection.getClass(
				"net.minecraftforge.fml.ModList",
				"net.neoforged.fml.ModList"
		) != null) {
			clazz = forgeMethods;
		} else {
			throw new RuntimeException("Couldn't find platform methods");
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