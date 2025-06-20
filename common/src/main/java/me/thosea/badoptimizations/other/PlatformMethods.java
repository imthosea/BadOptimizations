package me.thosea.badoptimizations.other;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

// don't throw dummy exception so intellij doesn't mark code as unreachable
public final class PlatformMethods {
	private PlatformMethods() {}

	@ExpectPlatform
	public static String getVersion() {
		return "";
	}

	@ExpectPlatform
	public static File getConfigFolder() {
		return new File(".");
	}

	@ExpectPlatform
	public static boolean isModLoaded(String id) {
		return true;
	}

	@ExpectPlatform
	public static boolean isOnServer() {return true;}

	@ExpectPlatform
	public static InputStream streamConfigTemplate() {return new ByteArrayInputStream(new byte[0]);}
}