package me.thosea.badoptimizations.platform;

@SuppressWarnings("unused")
public final class BadMcVersion {
	private BadMcVersion() {}

	public static final int MAJOR; // excluding the 1.
	public static final int MINOR;

	static {
		@SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
		String[] split = PlatformMethods.INSTANCE.getMinecraftVersion().split("[.-]");
		assert split.length >= 2;
		if(split[0].equals("1")) {
			MAJOR = tryParse(split[1], 0);
			MINOR = split.length < 3 ? 0 : tryParse(split[2], 0);
		} else {
			MAJOR = tryParse(split[0], 0);
			MINOR = tryParse(split[1], 0);
		}
	}

	private static int tryParse(String str, int def) {
		try {
			return Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return def;
		}
	}

	public static boolean isAtMost(int major, int minor) { // <=
		return MAJOR <= major && MINOR <= minor;
	}
	public static boolean isAtLeast(int major, int minor) { // >=
		return MAJOR >= major && MINOR >= minor;
	}
}