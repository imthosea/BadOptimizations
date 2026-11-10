package me.thosea.badoptimizations.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

// "bad" (optimizations) reflection utils
public final class BadReflection {
	private BadReflection() {}

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.publicLookup();

	public static Class<?> getClass(String... clazzes) {
		for(String clazz : clazzes) {
			try {
				return Class.forName(clazz);
			} catch(ClassNotFoundException ignored) {}
		}
		return null;
	}

	public static MethodHandle getVirtual(
			Class<?> clazz, MethodType signature,
			String... names
	) {
		for(String name : names) {
			try {
				return LOOKUP.findVirtual(clazz, name, signature);
			} catch(NoSuchMethodException | IllegalAccessException ignored) {}
		}
		throw new AssertionError("Couldn't find virtual method with names " + Arrays.toString(names));
	}
}