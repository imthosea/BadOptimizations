package me.thosea.badoptimizations.utils;

import me.thosea.badoptimizations.reflect.BadReflection;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Objects;

// 1.20.5 - 1.21.11
// i forgot you can have multiple static blocks in this language
public final class BadVersionHacks {
	private BadVersionHacks() {}

	/* make sure all the MethodHandles are static final so JIT inlines into a normal call */

	private static final Minecraft client = Objects.requireNonNull(Minecraft.getInstance());

	// 1.20.5/1.20.6:
	// minecraft.getFrameTime();
	// 1.21.1+:
	// minecraft.getTimer().getGameTimeDeltaPartialTick(false);
	private static final Object objDeltaTracker; // 1.21.1+ -> DeltaTracker, 1.20.5/6 -> null
	private static final MethodHandle getPartialTick;

	static {
		Class<?> timerClass = BadReflection.getClass(
				"net.minecraft.client.DeltaTracker",
				"net.minecraft.class_9779"
		);
		if(timerClass != null) { // 1.21.1+
			var getTimer = BadReflection.getVirtual(
					Minecraft.class,
					MethodType.methodType(timerClass),
					"getTimer", "method_60646", "getDeltaTracker", "method_61966"
			);
			try {
				objDeltaTracker = getTimer.invoke(client);
			} catch(Throwable e) {
				throw new RuntimeException(e);
			}
			getPartialTick = BadReflection.getVirtual(
					timerClass,
					MethodType.methodType(float.class, boolean.class),
					"getGameTimeDeltaPartialTick", "method_60637"
			);
		} else { // 1.20.5/1.20.6
			objDeltaTracker = null;
			getPartialTick = BadReflection.getVirtual(
					Minecraft.class,
					MethodType.methodType(float.class),
					"getFrameTime", "method_1488"
			);
		}
	}

	public static float getPartialTick() {
		try {
			return objDeltaTracker != null
					? (float) getPartialTick.invoke(objDeltaTracker, /*ignoreFrozenGame*/ false)
					: (float) getPartialTick.invokeExact(client);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("UNCHECKED_CAST")
	public static <T> T cast(Object object) {
		return (T) object;
	}

	public static int toColorInt(double r, double g, double b) {
		// from 1.21.2+ ARGB.color
		int x = Mth.floor(r * 255.0F);
		int y = Mth.floor(g * 255.0F);
		int z = Mth.floor(b * 255.0F);
		return 255 << 24 | x << 16 | y << 8 | z;
	}
	public static Vec3 toColorVec(int color) {
		// from 1.21.2+ ARGB.vector3fFromRGB24
		float x = (float) (color >> 16 & 255) / 255.0F;
		float y = (float) (color >> 8 & 255) / 255.0F;
		float z = (float) (color & 255) / 255.0F;
		return new Vec3(x, y, z);
	}
}