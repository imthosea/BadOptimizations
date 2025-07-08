package me.thosea.badoptimizations.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

// don't throw dummy exception so intellij doesn't mark code as unreachable
public final class PlatformMethods {
	private PlatformMethods() {}

	@ExpectPlatform
	public static String getVersion() {
		return "";
	}

	@ExpectPlatform
	public static Path getConfigFolder() {
		return Paths.get("mario");
	}

	@ExpectPlatform
	public static boolean isModLoaded(String id) {
		return true;
	}

	@ExpectPlatform
	public static InputStream streamConfigTemplate() {return new ByteArrayInputStream(new byte[0]);}

	@ExpectPlatform
	public static Map<String, List<String>> getModIncompatibilities() {return Map.of();}
}