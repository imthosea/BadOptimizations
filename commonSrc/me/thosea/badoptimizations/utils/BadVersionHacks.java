package me.thosea.badoptimizations.utils;

public final class BadVersionHacks {
	private BadVersionHacks() {}

	@SuppressWarnings("UNCHECKED_CAST")
	public static <T> T cast(Object object) {
		return (T) object;
	}
}