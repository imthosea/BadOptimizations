package me.thosea.badoptimizations.hook;

import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

import static me.thosea.badoptimizations.config.Config.LOGGER;

public final class HookCreator {
	private HookCreator() {}

	public static final String INVALID_HOOK_MESSAGE = "Mod {} specifies an invalid BadOptimizations caching hook";

	@Nullable
	public static BooleanSupplier tryCreateHook(String modId, String clazzName) {
		if(clazzName == null) return null;

		Class<?> clazz;
		try {
			clazz = Class.forName(clazzName);
		} catch(ClassNotFoundException e) {
			LOGGER.warn(INVALID_HOOK_MESSAGE, modId);
			LOGGER.warn("No class named \"{}\"", clazzName);
			return null;
		}

		if(!clazz.isAssignableFrom(BooleanSupplier.class)) {
			LOGGER.warn(INVALID_HOOK_MESSAGE, modId);
			LOGGER.warn("Class {} does not implement java.util.function.BooleanSupplier", clazzName);
			return null;
		}

		try {
			return (BooleanSupplier) clazz.getConstructor().newInstance();
		} catch(NoSuchMethodException e) {
			LOGGER.warn(INVALID_HOOK_MESSAGE, modId);
			LOGGER.warn("Class {} does not have a public empty default constructor", clazzName);
			return null;
		} catch(Exception e) {
			LOGGER.warn("Failed to create caching hooks for mod {}", modId);
			LOGGER.warn("", e);
			return null;
		}
	}
}