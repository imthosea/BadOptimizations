package me.thosea.badoptimizations.mixin.skyangle;

import me.thosea.badoptimizations.interfaces.LevelMethods;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

// 1.21.10-
@Mixin(Level.class)
public final class MixinLevel implements LevelMethods {
	private long bo$lastTimeOfDayTime = -1;
	private float bo$lastTimeOfDay;

	@Override
	public long bo$getTimeOfDayLastTime() {
		return bo$lastTimeOfDayTime;
	}
	@Override
	public float bo$getTimeOfDayCache() {
		return bo$lastTimeOfDay;
	}
	@Override
	public void bo$setTimeOfDayCache(long time, float value) {
		bo$lastTimeOfDayTime = time;
		bo$lastTimeOfDay = value;
	}
}