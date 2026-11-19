package me.thosea.badoptimizations.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

// "bad" (optimizations) reflection utils
public final class BadReflection {
	private BadReflection() {}

	public static Class<?> getClass(String... clazzes) {
		for(String clazz : clazzes) {
			try {
				// can't use Class.forName cause neoforge
				return Thread.currentThread().getContextClassLoader().loadClass(clazz);
			} catch(ClassNotFoundException ignored) {}
		}
		return null;
	}

	public static MethodHandle getVirtual(Class<?> clazz, MethodType signature, String... names) {
		for(String name : names) {
			try {
				return MethodHandles.publicLookup().findVirtual(clazz, name, signature);
			} catch(NoSuchMethodException | IllegalAccessException ignored) {}
		}
		throw new AssertionError("Couldn't find virtual method with names " + Arrays.toString(names));
	}

	public static MethodHandle getStatic(
			MethodHandles.Lookup lookup,
			Class<?> clazz, MethodType signature, String... names
	) {
		for(String name : names) {
			try {
				return lookup.findStatic(clazz, name, signature);
			} catch(NoSuchMethodException | IllegalAccessException ignored) {}
		}
		throw new AssertionError("Couldn't find static method with names " + Arrays.toString(names));
	}

	public static MethodHandle getStatic(Class<?> clazz, MethodType signature, String... names) {
		return getStatic(MethodHandles.publicLookup(), clazz, signature, names);
	}
}