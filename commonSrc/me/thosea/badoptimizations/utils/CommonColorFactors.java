package me.thosea.badoptimizations.utils;

import me.thosea.badoptimizations.hook.CacheHooks;
import me.thosea.badoptimizations.mixin.tick.MixinClientWorld;
import me.thosea.badoptimizations.mixin.tick.MixinLightTexture;
import me.thosea.badoptimizations.reflect.BadReflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Objects;

/**
 * Holds common factors for {@link MixinClientWorld} & {@link MixinLightTexture}
 */
public final class CommonColorFactors {
	private CommonColorFactors() {}

	private static final Minecraft CLIENT = Objects.requireNonNull(Minecraft.getInstance(), "loaded too early");

	/* make sure all of this is static final so the JIT inlines it into a normal call!! */
	private static final Object deltaTracker; // 1.21.1 -> DeltaTracker, 1.20.5/6 -> null
	private static final MethodHandle getPartialTick;

	static {
		Class<?> timerClass = BadReflection.getClass(
				"net.minecraft.client.DeltaTracker",
				"net.minecraft.class_9779"
		);
		if(timerClass != null) { // 1.21.1
			// minecraft.getTimer().getGameTimeDeltaPartialTick(false);
			var getTimer = BadReflection.getVirtual(
					Minecraft.class,
					MethodType.methodType(timerClass),
					"getTimer", "method_60646"
			);
			try {
				deltaTracker = getTimer.invoke(CLIENT);
			} catch(Throwable e) {
				throw new RuntimeException(e);
			}
			getPartialTick = BadReflection.getVirtual(
					timerClass,
					MethodType.methodType(float.class, boolean.class),
					"getGameTimeDeltaPartialTick", "method_60637"
			);
		} else { // 1.20.5/1.20.6
			deltaTracker = null;
			// minecraft.getFrameTime();
			getPartialTick = BadReflection.getVirtual(
					Minecraft.class,
					MethodType.methodType(float.class),
					"getFrameTime", "method_1488"
			);
		}
	}

	public static final CommonColorFactors SKY_COLOR = new CommonColorFactors();
	public static final CommonColorFactors LIGHTMAP = new CommonColorFactors();

	private static int lastUpdateTick = -1;

	public static float lastRainGradient;
	public static float lastThunderGradient;
	public static int lastLightningTicks;

	public static float rainGradientMultiplier;
	public static float thunderGradientMultiplier;

	private boolean thisInstanceDirty;
	private boolean didTickChange;
	private long lastTime;

	public static void tick() {
		int tick = CLIENT.player.tickCount;
		if(lastUpdateTick == tick) return;
		lastUpdateTick = tick;

		ClientLevel world = CLIENT.level;
		// jit do your thing
		float tickDelta;
		try {
			tickDelta = deltaTracker != null
					? (float) getPartialTick.invoke(deltaTracker, /*ignoreFrozenGame*/ false)
					: (float) getPartialTick.invokeExact(CLIENT);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
		boolean result = false;

		float rainGradient = world.getRainLevel(tickDelta);
		float thunderGradient = world.getThunderLevel(tickDelta);
		int lightningTicks = world.getSkyFlashTime();

		if(rainGradient != lastRainGradient) {
			result = true;
			lastRainGradient = rainGradient;

			if(rainGradient > 0) {
				rainGradientMultiplier = 1.0f - rainGradient * 0.75F;
			} else {
				rainGradientMultiplier = 0;
			}
		}
		if(thunderGradient != lastThunderGradient) {
			result = true;
			lastThunderGradient = thunderGradient;
			if(thunderGradient > 0) {
				thunderGradientMultiplier = 1.0f - thunderGradient * 0.75F;
			} else {
				thunderGradientMultiplier = 0;
			}
		}
		if(lastLightningTicks != lightningTicks) {
			result = true;
			lastLightningTicks = lightningTicks;
		}
		if(CacheHooks.invokeCommon()) {
			result = true;
		}

		SKY_COLOR.didTickChange = true;
		LIGHTMAP.didTickChange = true;

		if(result) {
			SKY_COLOR.thisInstanceDirty = true;
			LIGHTMAP.thisInstanceDirty = true;
		}
	}

	public boolean isDirty() {
		if(this.thisInstanceDirty) {
			this.thisInstanceDirty = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean didTickChange() {
		if(this.didTickChange) {
			this.didTickChange = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return in-game time passed since the last call of {@link #updateLastTime()}
	 */
	public long getTimeDelta() {
		return Math.abs(CLIENT.level.dayTime() - lastTime);
	}

	public void updateLastTime() {
		lastTime = CLIENT.level.dayTime();
	}
}