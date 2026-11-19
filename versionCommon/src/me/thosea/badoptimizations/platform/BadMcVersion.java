package me.thosea.badoptimizations.platform;

public final class BadMcVersion {
	private BadMcVersion() {}

	public static final int MAJOR; // excluding the 1.
	public static final int MINOR;

	static {
		@SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
		String[] split = PlatformMethods.INSTANCE.getMinecraftVersion().split("[.-]");
		assert split.length >= 2;
		if(split[0].equals("1")) {
			MAJOR = Integer.parseInt(split[1]);
			MINOR = split.length < 3 ? 0 : Integer.parseInt(split[2]);
		} else {
			MAJOR = Integer.parseInt(split[0]);
			MINOR = Integer.parseInt(split[1]);
		}
	}

	public static boolean isAtMost(int major, int minor) { // <=
		return MAJOR <= major && MINOR <= minor;
	}
	public static boolean isAtLeast(int major, int minor) { // >=
		return MAJOR >= major && MINOR >= minor;
	}
}