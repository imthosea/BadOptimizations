package me.thosea.badoptimizations.utils;

import me.thosea.badoptimizations.platform.BadMcVersion;
import me.thosea.badoptimizations.reflect.BadReflection;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URI;
import java.util.Objects;

// 1.21.10/1.21.11
public final class BadVersionHacks {
	private BadVersionHacks() {}

	private static final Class<?> identifierClass = BadReflection.requireClass(
			"net.minecraft.class_2960", // yarn
			"net.minecraft.resources.ResourceLocation", // 1.21.10
			"net.minecraft.resources.Identifier" // 1.21.11 (yarn tribute?)
	);
	private static final MethodHandle fromNamespaceAndPath = BadReflection.getStatic(
			identifierClass,
			MethodType.methodType(identifierClass, String.class, String.class),
			"fromNamespaceAndPath", "method_60655"
	);
	public static Object makeIdentifier(String namespace, String path) {
		try {
			return fromNamespaceAndPath.invoke(namespace, path);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void registerDebug(MethodHandles.Lookup lookup, Object identifier, DebugScreenEntry entry) {
		var debugRegister = BadReflection.getStatic(
				lookup,
				DebugScreenEntries.class,
				MethodType.methodType(identifierClass, identifierClass, DebugScreenEntry.class),
				"register", "method_72763"
		);
		try {
			debugRegister.invoke(identifier, entry);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	// 1.21.10
	// Biome.getSkyColor
	// 1.21.11
	// method get NUKED
	private static final MethodHandle getSkyColor = BadMcVersion.isAtMost(21, 10) ? BadReflection.getVirtual(
			Biome.class,
			MethodType.methodType(int.class),
			"getSkyColor", "method_8697"
	) : null;
	public static int getSkyColor(Biome biome) {
		// will get JIT'd out
		if(!BadMcVersion.isAtMost(21, 10)) {
			throw new AssertionError("too new version");
		}
		try {
			return (int) getSkyColor.invokeExact(biome);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	// 1.21.10
	// LevelTimeAccess.getTimeOfDay(float)
	// 1.21.11
	// method get nuked
	private static final Class<?> timeAccessClass = BadReflection.getClass(
			"net.minecraft.world.level.LevelTimeAccess",
			"net.minecraft.class_5424"
	);
	private static final MethodHandle getTimeOfDay = timeAccessClass != null ? BadReflection.getVirtual(
			timeAccessClass,
			MethodType.methodType(float.class, float.class),
			"getTimeOfDay", "method_30274"
	) : null;
	public static float getTimeOfDay(Level level, float partialTick) {
		Objects.requireNonNull(getTimeOfDay);
		try {
			return (float) getTimeOfDay.invoke(level, partialTick);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	// 1.21.10
	// Mth.cos(float)->float
	// 1.21.11
	// Mth.cos(double)->float
	private static final MethodHandle cos = BadReflection.getStatic(
			Mth.class,
			MethodType.methodType(float.class, BadMcVersion.isAtMost(21, 10) ? float.class : double.class),
			"cos", "method_15362"
	);
	public static float cos(float value) {
		try {
			return (float) cos.invokeExact(value);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	// from 1.21.10 Vec3.fromRGB24
	public static Vec3 fromRGB24(int rgb) {
		double d = (rgb >> 16 & 0xFF) / 255.0;
		double e = (rgb >> 8 & 0xFF) / 255.0;
		double f = (rgb & 0xFF) / 255.0;
		return new Vec3(d, e, f);
	}

	// 1.21.10:
	// net.minecraft.Util(.OS)
	// 1.21.11:
	// net.minecraft.util.Util(.OS)
	private static final Class<?> utilClass = BadReflection.requireClass(
			"net.minecraft.Util",
			"net.minecraft.util.Util",
			"net.minecraft.class_156"
	);
	private static final Class<?> utilOsClass = BadReflection.requireClass(
			"net.minecraft.Util$OS",
			"net.minecraft.util.Util$OS",
			"net.minecraft.class_156$class_158"
	);
	private static final Object objPlatform = yoloInvoke(BadReflection.getStatic(
			utilClass,
			MethodType.methodType(utilOsClass),
			"getPlatform", "method_668"
	));
	private static final MethodHandle openUri = BadReflection.getVirtual(
			utilOsClass,
			MethodType.methodType(void.class, URI.class),
			"openUri", "method_673"
	);

	public static void openUri(URI uri) {
		try {
			openUri.invoke(objPlatform, uri);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static Object yoloInvoke(MethodHandle method) {
		try {
			return method.invoke();
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("UNCHECKED_CAST")
	public static <T> T cast(Object object) {
		return (T) object;
	}
}